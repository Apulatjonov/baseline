package uz.signal;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class CompatibleModelProviderTest {
  @Test
  void fallsBackAfterPrimaryFailuresAndParsesStructuredSummary() throws Exception {
    var mapper = new ObjectMapper();
    var calls = new AtomicInteger();
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/primary/chat/completions",
        request -> {
          calls.incrementAndGet();
          request.sendResponseHeaders(503, -1);
          request.close();
        });
    server.createContext(
        "/fallback/chat/completions",
        request -> {
          var input = mapper.readTree(request.getRequestBody());
          assertThat(input.path("model").asText()).isEqualTo("test-fallback");
          assertThat(input.path("messages").get(1).path("content").asText())
              .isEqualTo("Source evidence");
          var summary =
              mapper.writeValueAsString(
                  new LanguageModelProvider.Synthesis("Sarlavha", "Xulosa", "Sabab", "Ta’sir"));
          byte[] body =
              mapper.writeValueAsBytes(
                  Map.of("choices", List.of(Map.of("message", Map.of("content", summary)))));
          request.sendResponseHeaders(200, body.length);
          request.getResponseBody().write(body);
          request.close();
        });
    server.start();
    try {
      String base = "http://127.0.0.1:" + server.getAddress().getPort();
      var provider =
          new CompatibleModelProvider(
              mapper,
              base + "/primary",
              "test-key",
              "test-primary",
              base + "/fallback",
              "test-key",
              "test-fallback");
      assertThat(provider.synthesize("Source evidence").title()).isEqualTo("Sarlavha");
      assertThat(calls.get()).isEqualTo(2);
    } finally {
      server.stop(0);
    }
  }

  @Test
  void noConfigurationFailsExplicitly() throws Exception {
    var provider = new CompatibleModelProvider(new ObjectMapper(), "", "", "", "", "", "");
    assertThatThrownBy(() -> provider.synthesize("Evidence"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("No language model configured");
  }
}
