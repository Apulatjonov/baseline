package uz.signal;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SourceRegistry {
  private final SourceRepository sources;
  private final EditorialService audit;

  SourceRegistry(SourceRepository sources, EditorialService audit) {
    this.sources = sources;
    this.audit = audit;
  }

  public record Configuration(
      String name,
      String domain,
      String sourceType,
      String country,
      String language,
      String rssUrl,
      double reliability,
      boolean enabled,
      int pollMinutes,
      String termsNotes) {}

  @Transactional
  public Source configure(String id, Configuration config, String actor) {
    if (!id.matches("[a-z0-9-]{1,64}")
        || blank(config.name())
        || blank(config.domain())
        || blank(config.country())
        || blank(config.language())
        || blank(config.termsNotes())
        || !Set.of("PRIMARY", "SECONDARY")
            .contains(config.sourceType() == null ? "" : config.sourceType())
        || !Double.isFinite(config.reliability())
        || config.reliability() < 0
        || config.reliability() > 1
        || config.pollMinutes() < 5
        || config.pollMinutes() > 1440) {
      throw invalid(
          "Provide source details, terms notes, reliability 0–1 and polling interval 5–1440"
              + " minutes");
    }
    String url = config.rssUrl() == null ? "" : config.rssUrl().trim();
    if (config.enabled() && url.isBlank()) throw invalid("Enabled sources need an RSS URL");
    if (!url.isBlank()) {
      try {
        var uri = URI.create(url);
        if (!"https".equals(uri.getScheme())
            || !config.domain().equals(uri.getHost())
            || uri.getUserInfo() != null
            || uri.getFragment() != null
            || (uri.getPort() != -1 && uri.getPort() != 443)) throw new IllegalArgumentException();
      } catch (IllegalArgumentException ex) {
        throw invalid(
            "Feed must use HTTPS on the exact registry domain without credentials or a custom"
                + " port");
      }
    }
    var source = sources.findById(id).orElseGet(Source::new);
    if (source.id != null) audit.audit(null, "SOURCE_BEFORE_UPDATE", actor, source);
    source.id = id;
    source.name = config.name().trim();
    source.domain = config.domain();
    source.sourceType = config.sourceType();
    source.country = config.country();
    source.language = config.language();
    source.rssUrl = url;
    source.reliability = config.reliability();
    source.enabled = config.enabled();
    source.pollMinutes = config.pollMinutes();
    source.termsNotes = config.termsNotes();
    source.lastAttempt = null;
    sources.save(source);
    audit.audit(null, "SOURCE_CONFIGURED", actor, Map.of("id", id, "configuration", config));
    return source;
  }

  private boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private ResponseStatusException invalid(String message) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }
}
