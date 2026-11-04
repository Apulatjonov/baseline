package uz.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EditorialService {
  private final EventRepository events;
  private final AuditRepository audits;
  private final ArticleRepository articles;
  private final ObjectMapper mapper;

  EditorialService(EventRepository e, AuditRepository a, ArticleRepository ar, ObjectMapper m) {
    events = e;
    audits = a;
    articles = ar;
    mapper = m;
  }

  void audit(String id, String action, String actor, Object value) {
    try {
      var a = new Audit();
      a.id = UUID.randomUUID().toString();
      a.eventId = id;
      a.action = action;
      a.actor = actor;
      a.payload = mapper.writeValueAsString(value);
      a.createdAt = Instant.now().toString();
      audits.save(a);
    } catch (Exception ex) {
      throw new IllegalStateException("Could not write audit", ex);
    }
  }

  @Transactional
  public Event review(String id, String action, String actor, Map<String, String> changes) {
    Event e =
        events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    audit(id, "BEFORE_" + action, actor, e);
    if (action.equals("edit")) {
      if (e.status.equals("PUBLISHED") && changes.getOrDefault("correctionNote", "").isBlank())
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "A public correction note is required");
      e.title = changes.getOrDefault("title", e.title);
      e.summary = changes.getOrDefault("summary", e.summary);
      e.whyItMatters = changes.getOrDefault("whyItMatters", e.whyItMatters);
      e.uzbekistanImpact = changes.getOrDefault("uzbekistanImpact", e.uzbekistanImpact);
      if (e.title.isBlank() || e.summary.isBlank())
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Headline and summary are required");
      if (!e.demo) {
        String input =
            articles.findByEventId(id).stream()
                .map(a -> a.title + " " + a.body)
                .reduce("", (a, b) -> a + " " + b);
        var unsupported =
            Intelligence.unsupportedNumbers(
                input, e.title + " " + e.summary + " " + e.whyItMatters + " " + e.uzbekistanImpact);
        e.validationNotes =
            unsupported.isEmpty()
                ? "Human verification required"
                : "Unsupported numbers: " + unsupported;
      }
      e.status = "REVIEW";
    } else if (action.equals("approve")) {
      if (!e.status.equals("REVIEW"))
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "Only reviewed drafts may be published");
      if (e.validationNotes.startsWith("Unsupported")
          || e.validationNotes.startsWith("AI unavailable"))
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Resolve validation issues by editing before approval");
      if (articles.findByEventId(id).isEmpty())
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY, "At least one source is required");
      e.status = "PUBLISHED";
      e.publishedAt = Instant.now().toString();
    } else if (action.equals("reject")) {
      e.status = "REJECTED";
    } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown action");
    e.updatedAt = Instant.now().toString();
    events.save(e);
    audit(id, action.toUpperCase(Locale.ROOT), actor, changes);
    return e;
  }
}
