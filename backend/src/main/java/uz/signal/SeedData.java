package uz.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;

@Configuration
class SeedData {
  @Bean
  ApplicationRunner seed(
      SourceRepository sources,
      EventRepository events,
      ArticleRepository articles,
      ObjectMapper mapper,
      @Value("${signal.demo}") boolean demo) {
    return args -> {
      var json = mapper.readTree(new ClassPathResource("demo.json").getInputStream());
      boolean newRegistry = sources.count() == 0;
      if (newRegistry)
        for (var node : json.path("sources")) sources.save(mapper.treeToValue(node, Source.class));
      // Defaults are applied only on first initialization; operator settings survive restarts.
      if (newRegistry) {
        var feeds = mapper.readTree(new ClassPathResource("feeds.json").getInputStream());
        for (var node : feeds) {
          var source = sources.findById(node.path("id").asText()).orElseGet(Source::new);
          source.id = node.path("id").asText();
          source.name = node.path("name").asText();
          source.country = node.path("country").asText();
          source.language = node.path("language").asText();
          source.rssUrl = node.path("rssUrl").asText();
          source.domain = java.net.URI.create(source.rssUrl).getHost();
          source.sourceType = node.path("sourceType").asText("PRIMARY");
          source.reliability = node.path("reliability").asDouble(0.95);
          source.enabled = true;
          source.pollMinutes = 30;
          source.termsNotes =
              "RSS metadata and short excerpts for editorial review; retain attribution and source"
                  + " links. Company announcements are issuer claims, not independent"
                  + " verification.";
          sources.save(source);
        }
      }
      if (demo && events.count() == 0) {
        for (var node : json.path("events")) {
          var copy = node.deepCopy();
          ((com.fasterxml.jackson.databind.node.ObjectNode) copy).remove("sources");
          var e = mapper.treeToValue(copy, Event.class);
          events.save(e);
          for (var src : node.path("sources")) {
            var a = new Article();
            a.id = UUID.randomUUID().toString();
            a.eventId = e.id;
            a.sourceId = src.path("id").asText();
            a.url = src.path("url").asText();
            a.title = e.title;
            a.body =
                "Demonstration content; institution homepage is a reference, not evidence of a"
                    + " current claim.";
            a.contentHash = e.id + "-" + a.sourceId;
            a.publishedAt = e.publishedAt;
            a.retrievedAt = e.publishedAt;
            articles.save(a);
          }
        }
        var draft = new Event();
        draft.id = "review-demo";
        draft.title = "Pul o‘tkazmalari: tahrir uchun namuna";
        draft.category = "fintech";
        draft.summary =
            "Bu tahrir jarayonini sinash uchun tayyorlangan namuna. Nashr qilishdan oldin manbalar"
                + " va faktlarni tekshiring.";
        draft.whyItMatters = "To‘lov xarajatlari va xizmat tezligini taqqoslash muhim.";
        draft.uzbekistanImpact =
            "Pul o‘tkazmalarini oluvchi oilalar uchun ahamiyatli bo‘lishi mumkin.";
        draft.countries = "UZ";
        draft.entities = "Payments";
        draft.status = "REVIEW";
        draft.confidence = "MEDIUM";
        draft.editorialLabel = "AI SUMMARY";
        draft.importance = .86;
        draft.uzbekistanScore = .9;
        draft.publishedAt = "2026-09-30T10:00:00Z";
        draft.updatedAt = draft.publishedAt;
        draft.demo = true;
        draft.validationNotes = "Demonstration draft; human verification required";
        events.save(draft);
        var a = new Article();
        a.id = UUID.randomUUID().toString();
        a.eventId = draft.id;
        a.sourceId = "bis";
        a.url = "https://www.bis.org";
        a.title = draft.title;
        a.body = draft.summary;
        a.contentHash = "review-demo-bis";
        a.publishedAt = draft.publishedAt;
        a.retrievedAt = draft.publishedAt;
        articles.save(a);
      }
    };
  }
}
