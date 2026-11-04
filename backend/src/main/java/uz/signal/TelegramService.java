package uz.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TelegramService {
  private final String token, chat;
  private final PublicationRepository publications;
  private final EventRepository events;
  private final ArticleRepository articles;
  private final ObjectMapper mapper;
  private final EditorialService audit;

  TelegramService(
      @Value("${signal.telegram-token}") String t,
      @Value("${signal.telegram-chat}") String c,
      PublicationRepository p,
      EventRepository e,
      ArticleRepository a,
      ObjectMapper m,
      EditorialService ed) {
    token = t;
    chat = c;
    publications = p;
    events = e;
    articles = a;
    mapper = m;
    audit = ed;
  }

  public synchronized Publication publish(String id, String actor) throws Exception {
    if (token.isBlank() || chat.isBlank())
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "Telegram credentials are not configured");
    var e =
        events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (e.demo || !e.status.equals("PUBLISHED"))
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Only approved non-demo events can be sent");
    var existing = publications.findByEventIdAndChannel(id, "TELEGRAM");
    if (existing.isPresent())
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Publication already attempted; inspect status before retrying to prevent duplicates");
    String body =
        e.title
            + "\n\n"
            + e.summary
            + "\n\nNega muhim?\n"
            + e.whyItMatters
            + "\n\nO‘zbekiston uchun:\n"
            + e.uzbekistanImpact
            + "\n\nManbalar:\n"
            + String.join(
                "\n", articles.findByEventId(id).stream().map(a -> a.url).distinct().toList());
    if (body.length() > 4000)
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Shorten the brief before sending");
    var p = new Publication();
    p.id = UUID.randomUUID().toString();
    p.eventId = id;
    p.channel = "TELEGRAM";
    p.status = "SENDING";
    p.body = body;
    p.createdAt = Instant.now().toString();
    publications.save(p);
    try {
      var req =
          HttpRequest.newBuilder(
                  URI.create("https://api.telegram.org/bot" + token + "/sendMessage"))
              .timeout(Duration.ofSeconds(20))
              .header("Content-Type", "application/json")
              .POST(
                  HttpRequest.BodyPublishers.ofString(
                      mapper.writeValueAsString(
                          Map.of(
                              "chat_id",
                              chat,
                              "text",
                              body,
                              "link_preview_options",
                              Map.of("is_disabled", true)))))
              .build();
      var res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
      var json = mapper.readTree(res.body());
      if (res.statusCode() != 200 || !json.path("ok").asBoolean())
        throw new IllegalStateException("Telegram rejected publication");
      p.status = "SENT";
      p.externalId = json.path("result").path("message_id").asText();
      publications.save(p);
      audit.audit(id, "TELEGRAM_SENT", actor, Map.of("externalId", p.externalId));
      return p;
    } catch (Exception ex) {
      p.status = "UNKNOWN_OR_FAILED";
      publications.save(p);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          "Telegram delivery failed or is uncertain; inspect channel before retrying");
    }
  }
}
