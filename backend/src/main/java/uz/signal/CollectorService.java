package uz.signal;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.*;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CollectorService {
  private final SourceRepository sources;
  private final ArticleRepository articles;
  private final EventRepository events;
  private final LanguageModelProvider ai;
  private final EditorialService editorial;
  private final MeterRegistry metrics;
  private final boolean enabled;
  private final HttpClient client =
      HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(10))
          .followRedirects(HttpClient.Redirect.NEVER)
          .build();

  CollectorService(
      SourceRepository s,
      ArticleRepository a,
      EventRepository e,
      LanguageModelProvider ai,
      EditorialService ed,
      MeterRegistry m,
      @Value("${signal.collection-enabled}") boolean enabled) {
    sources = s;
    articles = a;
    events = e;
    this.ai = ai;
    editorial = ed;
    metrics = m;
    this.enabled = enabled;
  }

  @Scheduled(fixedDelay = 60000, initialDelay = 30000)
  public void scheduled() {
    if (enabled) {
      collect(false);
      retryFailedSynthesis();
    }
  }

  // Persisted draft state survives restarts. Run only one backend worker per database.
  public synchronized int retryFailedSynthesis() {
    int retried = 0;
    for (var event : events.findAll()) {
      if (retried >= 10) break;
      if (!event.demo
          && "REVIEW".equals(event.status)
          && event.validationNotes != null
          && event.validationNotes.startsWith("AI unavailable")
          && event.synthesisAttempts < 5
          && (event.synthesisRetryAt == null
              || !Instant.parse(event.synthesisRetryAt).isAfter(Instant.now()))) {
        synthesize(event);
        retried++;
      }
    }
    return retried;
  }

  public synchronized Map<String, Object> collect(boolean force) {
    int count = 0;
    int failures = 0;
    for (var source : sources.findAll()) {
      if (!source.enabled || source.rssUrl == null || source.rssUrl.isBlank()) continue;
      if (!force
          && source.lastAttempt != null
          && Instant.parse(source.lastAttempt)
              .plusSeconds(source.pollMinutes * 60L)
              .isAfter(Instant.now())) continue;
      try {
        source.lastAttempt = Instant.now().toString();
        sources.save(source);
        URI uri = URI.create(source.rssUrl);
        if (!"https".equals(uri.getScheme()) || !uri.getHost().equals(source.domain))
          throw new IllegalArgumentException("Feed must use registry HTTPS domain");
        for (var address : java.net.InetAddress.getAllByName(uri.getHost()))
          if (address.isAnyLocalAddress()
              || address.isLoopbackAddress()
              || address.isSiteLocalAddress()
              || address.isLinkLocalAddress())
            throw new IllegalArgumentException("Private feed address blocked");
        var req =
            HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", "Baseline/0.1 RSS reader")
                .GET()
                .build();
        var request = client.sendAsync(req, info -> new BoundedBodySubscriber(2_000_000));
        try {
          // The total deadline includes the response body, not just arrival of headers.
          var response = request.get(25, java.util.concurrent.TimeUnit.SECONDS);
          if (response.statusCode() != 200)
            throw new IOException("Feed HTTP " + response.statusCode());
          byte[] bytes = response.body();
          var input = new SyndFeedInput();
          input.setAllowDoctypes(false);
          var feed = input.build(new XmlReader(new ByteArrayInputStream(bytes)));
          for (var item : feed.getEntries().stream().limit(15).toList())
            if (ingest(source, item)) {
              count++;
              source.articlesCollected++;
            }
        } finally {
          if (!request.isDone()) request.cancel(true);
        }
        source.lastSuccess = Instant.now().toString();
        source.lastFailure = null;
        metrics.counter("source_fetch_success_total").increment();
      } catch (Exception ex) {
        source.lastFailure = Instant.now() + " · " + ex.getClass().getSimpleName();
        failures++;
        metrics.counter("source_fetch_failure_total").increment();
      }
      sources.save(source);
    }
    return Map.of("ingested", count, "failedSources", failures);
  }

  boolean ingest(Source source, SyndEntry item) throws Exception {
    String title = Jsoup.parse(Objects.toString(item.getTitle(), "")).text();
    String body =
        Jsoup.parse(
                item.getDescription() == null
                    ? ""
                    : Objects.toString(item.getDescription().getValue(), ""))
            .text();
    if (title.isBlank() || item.getLink() == null) return false;
    URI articleUrl;
    try {
      articleUrl = URI.create(source.rssUrl).resolve(item.getLink().trim());
      if (!("https".equals(articleUrl.getScheme()) || "http".equals(articleUrl.getScheme()))
          || articleUrl.getHost() == null || articleUrl.getUserInfo() != null) return false;
    } catch (IllegalArgumentException ex) { return false; }
    String hash =
        HexFormat.of()
            .formatHex(
                MessageDigest.getInstance("SHA-256")
                    .digest(
                        (source.id + " " + title + " " + body).getBytes(StandardCharsets.UTF_8)));
    if (articles.existsByContentHash(hash)) return false;
    var now = Instant.now();
    var date = item.getPublishedDate() == null ? now : item.getPublishedDate().toInstant();
    Event event = null;
    for (var previous : articles.findAll())
      if (Math.abs(Duration.between(Instant.parse(previous.publishedAt), date).toHours()) < 24
          && Intelligence.similarity(previous.title, title) > .65) {
        event = events.findById(previous.eventId).orElse(null);
        if (event != null && !event.demo) break;
        event = null;
      }
    boolean created = event == null;
    if (created) {
      event = new Event();
      event.id = UUID.randomUUID().toString();
      event.title = title;
      event.summary = "Tahrirchi tekshiruvi va o‘zbekcha xulosa kutilmoqda.";
      event.category = Intelligence.category(title + " " + body);
      event.whyItMatters = "";
      event.uzbekistanImpact = "";
      event.countries = source.country;
      event.entities = source.name;
      event.status = "REVIEW";
      event.confidence = "LOW";
      event.editorialLabel = "AI SUMMARY";
      event.importance = Intelligence.score(source.reliability, .7, 1, .4, .5, .3);
      event.uzbekistanScore = .4;
      event.publishedAt = date.toString();
      event.updatedAt = now.toString();
      event.validationNotes = "AI unavailable: manual synthesis required";
      events.save(event);
      metrics.counter("events_created_total").increment();
    }
    var article = new Article();
    article.id = UUID.randomUUID().toString();
    article.sourceId = source.id;
    article.eventId = event.id;
    article.url = articleUrl.toString();
    article.title = title;
    article.body = body;
    article.contentHash = hash;
    article.publishedAt = date.toString();
    article.retrievedAt = now.toString();
    articles.save(article);
    metrics.counter("articles_ingested_total").increment();
    // Never silently replace already published text as additional reporting arrives.
    if (event.status.equals("REVIEW")) {
      synthesize(event);
    }
    return true;
  }

  private void synthesize(Event event) {
    var evidence =
        articles.findByEventId(event.id).stream()
            .map(a -> a.title + "\n" + a.body)
            .reduce("", (a, b) -> a + "\n\n" + b);
    long start = System.currentTimeMillis();
    event.synthesisAttempts++;
    try {
      metrics.counter("ai_calls_total").increment();
      var result = ai.synthesize(evidence);
      event.title = result.title();
      event.summary = result.summary();
      event.whyItMatters = result.whyItMatters();
      event.uzbekistanImpact = result.uzbekistanImpact();
      var missing =
          Intelligence.unsupportedNumbers(
              evidence,
              String.join(
                  " ",
                  result.title(),
                  result.summary(),
                  result.whyItMatters(),
                  result.uzbekistanImpact()));
      event.validationNotes =
          missing.isEmpty() ? "Human verification required" : "Unsupported numbers: " + missing;
      event.confidence = "MEDIUM";
      event.synthesisRetryAt = null;
      editorial.audit(
          event.id,
          "AI_SYNTHESIS",
          "system",
          Map.of(
              "prompt",
              "uz-synthesis-v1",
              "inputHash",
              evidenceHash(evidence),
              "output",
              result,
              "latencyMs",
              System.currentTimeMillis() - start));
    } catch (Exception ex) {
      event.synthesisRetryAt =
          Instant.now()
              .plusSeconds(Math.min(3600, 60L * (1L << Math.min(event.synthesisAttempts, 6))))
              .toString();
      metrics.counter("ai_errors_total").increment();
      editorial.audit(
          event.id,
          "AI_FAILED",
          "system",
          Map.of("prompt", "uz-synthesis-v1", "error", ex.getClass().getSimpleName()));
    }
    event.updatedAt = Instant.now().toString();
    events.save(event);
  }

  private String evidenceHash(String evidence) throws Exception {
    return HexFormat.of()
        .formatHex(
            MessageDigest.getInstance("SHA-256").digest(evidence.getBytes(StandardCharsets.UTF_8)));
  }
}
