package uz.signal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
  private final EventRepository events;
  private final SourceRepository sources;
  private final ArticleRepository articles;
  private final AuditRepository audits;
  private final PublicationRepository publications;
  private final EditorialService editorial;
  private final CollectorService collector;
  private final TelegramService telegram;
  private final ObjectMapper mapper;
  private final boolean demo;

  ApiController(
      EventRepository e,
      SourceRepository s,
      ArticleRepository a,
      AuditRepository au,
      PublicationRepository p,
      EditorialService ed,
      CollectorService c,
      TelegramService t,
      ObjectMapper m,
      @Value("${signal.demo}") boolean demo) {
    events = e;
    sources = s;
    articles = a;
    audits = au;
    publications = p;
    editorial = ed;
    collector = c;
    telegram = t;
    mapper = m;
    this.demo = demo;
  }

  Map<String, Object> view(Event e) {
    var result = mapper.convertValue(e, new TypeReference<Map<String, Object>>() {});
    result.put(
        "sources",
        articles.findByEventId(e.id).stream()
            .map(
                a -> {
                  var s = sources.findById(a.sourceId).orElseThrow();
                  return Map.of(
                      "id",
                      s.id,
                      "name",
                      s.name,
                      "url",
                      a.url,
                      "primary",
                      s.sourceType.equals("PRIMARY"));
                })
            .toList());
    result.put(
        "corrections",
        audits.findByEventIdOrderByCreatedAtDesc(e.id).stream()
            .filter(a -> a.action.equals("EDIT"))
            .map(
                a -> {
                  try {
                    var payload = mapper.readTree(a.payload);
                    return Map.of(
                        "date", a.createdAt, "note", payload.path("correctionNote").asText());
                  } catch (Exception ex) {
                    return Map.of("date", a.createdAt, "note", "");
                  }
                })
            .filter(c -> !c.get("note").isBlank())
            .toList());
    return result;
  }

  @GetMapping("/events")
  List<Map<String, Object>> list(
      @RequestParam(defaultValue = "") String q,
      @RequestParam(defaultValue = "") String category,
      @RequestParam(defaultValue = "") String country,
      @RequestParam(defaultValue = "") String entity) {
    return events.findAll().stream()
        .filter(e -> e.status.equals("PUBLISHED") && (demo || !e.demo))
        .filter(e -> category.isBlank() || category.equals(e.category))
        .filter(e -> country.isBlank() || e.countries.contains(country))
        .filter(
            e ->
                entity.isBlank()
                    || e.entities
                        .toLowerCase(Locale.ROOT)
                        .contains(entity.toLowerCase(Locale.ROOT)))
        .filter(
            e ->
                q.isBlank()
                    || (e.title + " " + e.summary + " " + e.entities + " " + e.countries)
                        .toLowerCase(Locale.ROOT)
                        .contains(q.toLowerCase(Locale.ROOT)))
        .sorted(Comparator.comparingDouble((Event e) -> e.importance).reversed())
        .limit(200)
        .map(this::view)
        .toList();
  }

  @GetMapping("/events/{id}")
  Map<String, Object> detail(@PathVariable String id) {
    var e =
        events
            .findById(id)
            .filter(x -> x.status.equals("PUBLISHED") && (demo || !x.demo))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return view(e);
  }

  @GetMapping("/search")
  List<Map<String, Object>> search(@RequestParam String q) {
    return list(q, "", "", "");
  }

  @GetMapping("/topics/{topic}")
  List<Map<String, Object>> topic(@PathVariable String topic) {
    return list("", topic, "", "");
  }

  @GetMapping("/countries/{country}")
  List<Map<String, Object>> country(@PathVariable String country) {
    return list("", "", country, "");
  }

  @GetMapping("/organizations/{entity}")
  List<Map<String, Object>> entity(@PathVariable String entity) {
    return list("", "", "", entity.replace('-', ' '));
  }

  @GetMapping("/briefs/morning")
  Map<String, Object> morning() {
    return Map.of(
        "title",
        "Bugun bilishingiz kerak bo‘lgan 5 voqea",
        "events",
        list("", "", "", "").stream().limit(5).toList(),
        "demo",
        demo);
  }

  @GetMapping("/sources")
  List<Source> sources() {
    return sources.findAll();
  }

  @GetMapping("/status")
  Map<String, Object> status() {
    return Map.of(
        "demo",
        demo,
        "sources",
        sources.count(),
        "enabledSources",
        sources.findAll().stream().filter(s -> s.enabled).count(),
        "events",
        list("", "", "", "").size());
  }

  @GetMapping("/admin/events")
  List<Map<String, Object>> queue() {
    return events.findAll().stream()
        .filter(e -> demo || !e.demo)
        .sorted(Comparator.comparing((Event e) -> e.updatedAt).reversed())
        .map(this::view)
        .toList();
  }

  @GetMapping("/admin/events/{id}/audit")
  List<Audit> audit(@PathVariable String id) {
    return audits.findByEventIdOrderByCreatedAtDesc(id);
  }

  @GetMapping("/admin/articles")
  List<Article> articles() {
    return articles.findAll();
  }

  @GetMapping("/admin/publications")
  List<Publication> publications() {
    return publications.findAll();
  }

  @PostMapping("/admin/events/{id}/{action}")
  Map<String, Object> review(
      @PathVariable String id,
      @PathVariable String action,
      @RequestBody(required = false) Map<String, String> changes,
      Principal principal) {
    return view(
        editorial.review(id, action, principal.getName(), changes == null ? Map.of() : changes));
  }

  @PostMapping("/admin/collect")
  Map<String, Object> collect(Principal principal) {
    editorial.audit(null, "COLLECT_REQUESTED", principal.getName(), Map.of());
    return collector.collect(true);
  }

  @PostMapping("/admin/synthesis/retry")
  Map<String, Object> retrySynthesis(Principal principal) {
    editorial.audit(null, "SYNTHESIS_RETRY_REQUESTED", principal.getName(), Map.of());
    return Map.of("retried", collector.retryFailedSynthesis());
  }

  @PatchMapping("/admin/sources/{id}")
  Source update(
      @PathVariable String id, @RequestBody Map<String, Boolean> body, Principal principal) {
    var s =
        sources.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    boolean enabled = body.getOrDefault("enabled", s.enabled);
    if (enabled && (s.rssUrl == null || s.rssUrl.isBlank()))
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Configure and verify a feed URL in the registry first");
    s.enabled = enabled;
    sources.save(s);
    editorial.audit(
        null, "SOURCE_UPDATED", principal.getName(), Map.of("source", id, "enabled", s.enabled));
    return s;
  }

  @PostMapping("/admin/telegram/{id}")
  Publication send(@PathVariable String id, Principal principal) throws Exception {
    return telegram.publish(id, principal.getName());
  }
}
