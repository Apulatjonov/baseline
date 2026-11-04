package uz.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class CompatibleModelProvider implements LanguageModelProvider {
  private final ObjectMapper mapper;
  private final String[] urls, keys, models;
  private final String prompt;
  private final HttpClient client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

  public CompatibleModelProvider(
      ObjectMapper m,
      @Value("${signal.ai-url}") String u,
      @Value("${signal.ai-key}") String k,
      @Value("${signal.ai-model}") String model,
      @Value("${signal.ai-fallback-url}") String fu,
      @Value("${signal.ai-fallback-key}") String fk,
      @Value("${signal.ai-fallback-model}") String fm)
      throws Exception {
    mapper = m;
    urls = new String[] {u, fu};
    keys = new String[] {k, fk};
    models = new String[] {model, fm};
    prompt =
        new ClassPathResource("prompts/uz-synthesis-v1.txt")
            .getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
  }

  public Synthesis synthesize(String text) throws Exception {
    Exception failure = new IllegalStateException("No language model configured");
    for (int i = 0; i < urls.length; i++) {
      if (urls[i].isBlank() || keys[i].isBlank() || models[i].isBlank()) continue;
      for (int attempt = 0; attempt < 2; attempt++)
        try {
          var payload =
              Map.of(
                  "model",
                  models[i],
                  "temperature",
                  0.1,
                  "response_format",
                  Map.of("type", "json_object"),
                  "messages",
                  List.of(
                      Map.of("role", "system", "content", prompt),
                      Map.of("role", "user", "content", text)));
          var req =
              HttpRequest.newBuilder(URI.create(urls[i].replaceAll("/$", "") + "/chat/completions"))
                  .timeout(Duration.ofSeconds(45))
                  .header("Authorization", "Bearer " + keys[i])
                  .header("Content-Type", "application/json")
                  .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                  .build();
          var res = client.send(req, HttpResponse.BodyHandlers.ofString());
          if (res.statusCode() != 200)
            throw new IllegalStateException("AI HTTP " + res.statusCode());
          var value =
              mapper.readValue(
                  mapper
                      .readTree(res.body())
                      .path("choices")
                      .path(0)
                      .path("message")
                      .path("content")
                      .asText(),
                  Synthesis.class);
          if (value.title() == null
              || value.summary() == null
              || value.whyItMatters() == null
              || value.uzbekistanImpact() == null
              || value.title().isBlank()
              || value.summary().isBlank()
              || value.whyItMatters().isBlank()
              || value.uzbekistanImpact().isBlank())
            throw new IllegalArgumentException("Incomplete synthesis");
          return value;
        } catch (Exception ex) {
          failure = ex;
        }
    }
    throw failure;
  }
}
