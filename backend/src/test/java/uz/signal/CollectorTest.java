package uz.signal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rometools.rome.feed.synd.*;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:collector;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
      "signal.demo=false",
      "signal.jwt-secret=collector-tests-only-secret-32-bytes-minimum",
      "signal.collection-enabled=false"
    })
@DirtiesContext
class CollectorTest {
  @Autowired CollectorService collector;
  @Autowired SourceRepository sources;
  @Autowired EventRepository events;
  @Autowired ArticleRepository articles;
  @Autowired EditorialService editorial;
  @MockitoBean LanguageModelProvider ai;

  @Test
  void failedDraftCanRecoverWithoutReingestingDuplicateArticle() throws Exception {
    when(ai.synthesize(anyString())).thenThrow(new IllegalStateException("Provider unavailable"));
    var entry = new SyndEntryImpl();
    entry.setTitle("Distinct agricultural production outlook improves across Asia");
    entry.setLink("https://www.bis.org/test-recovery");
    collector.ingest(sources.findById("bis").orElseThrow(), entry);
    var draft =
        events.findAll().stream()
            .filter(e -> e.title.equals(entry.getTitle()))
            .findFirst()
            .orElseThrow();
    assertThat(draft.synthesisAttempts).isEqualTo(1);
    assertThat(draft.synthesisRetryAt).isNotBlank();
    assertThat(collector.retryFailedSynthesis()).isZero();
    draft.synthesisRetryAt = java.time.Instant.now().minusSeconds(1).toString();
    events.save(draft);
    doReturn(
            new LanguageModelProvider.Synthesis(
                "Qishloq xo‘jaligi",
                "Ishlab chiqarish istiqboli yaxshilandi.",
                "Taklif oshishi mumkin.",
                "Ta’siri tekshirilmoqda."))
        .when(ai)
        .synthesize(anyString());
    assertThat(collector.retryFailedSynthesis()).isEqualTo(1);
    var recovered = events.findById(draft.id).orElseThrow();
    assertThat(recovered.status).isEqualTo("REVIEW");
    assertThat(recovered.validationNotes).isEqualTo("Human verification required");
    assertThat(recovered.synthesisRetryAt).isNull();
    assertThat(recovered.synthesisAttempts).isEqualTo(2);
    articles.deleteAll(articles.findByEventId(draft.id));
    events.deleteById(draft.id);
  }

  @Test
  void duplicatesDoNotPublishAndMultipleSourcesRetainProvenance() throws Exception {
    when(ai.synthesize(anyString()))
        .thenReturn(
            new LanguageModelProvider.Synthesis(
                "Fed stavkasi 4.25%",
                "Fed stavkani 4.25% darajasida saqladi.",
                "Qarz qiymatiga ta’sir qilishi mumkin.",
                "Bilvosita ta’sir ehtimoli bor."));
    var entry = new SyndEntryImpl();
    entry.setTitle("Federal Reserve maintains policy interest rate at 4.25%");
    entry.setLink("https://www.federalreserve.gov/test-release");
    entry.setPublishedDate(new Date());
    var content = new SyndContentImpl();
    content.setValue("The Federal Reserve maintains the policy interest rate at 4.25%.");
    entry.setDescription(content);
    var fed = sources.findById("fed").orElseThrow();
    var bis = sources.findById("bis").orElseThrow();
    assertThat(collector.ingest(fed, entry)).isTrue();
    assertThat(collector.ingest(fed, entry)).isFalse();
    assertThat(collector.ingest(bis, entry)).isTrue();
    assertThat(events.count()).isEqualTo(1);
    assertThat(articles.count()).isEqualTo(2);
    var event = events.findAll().getFirst();
    assertThat(event.status).isEqualTo("REVIEW");
    assertThat(event.validationNotes).isEqualTo("Human verification required");
    when(ai.synthesize(anyString()))
        .thenReturn(
            new LanguageModelProvider.Synthesis("Fed 7.99%", "Fed 7.99%", "Ta’sir", "Ta’sir"));
    entry.setTitle("Federal Reserve maintains policy interest rate at 4.25% today");
    entry.setLink("https://www.federalreserve.gov/test-update");
    collector.ingest(fed, entry);
    var flagged = events.findAll().getFirst();
    assertThat(flagged.validationNotes).contains("Unsupported numbers");
    assertThatThrownBy(() -> editorial.review(flagged.id, "approve", "editor", java.util.Map.of()))
        .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
  }
}
