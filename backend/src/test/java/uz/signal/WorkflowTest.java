package uz.signal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:workflow;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
      "signal.demo=true",
      "signal.collection-enabled=false",
      "signal.admin-password=test-password-only"
    })
@AutoConfigureMockMvc
@DirtiesContext
class WorkflowTest {
  @Autowired MockMvc mvc;

  @Test
  void sourceConfigurationRejectsInsecureUrlsAndAcceptsValidRegistryUpdate() throws Exception {
    var auth = jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"));
    String config =
        """
        {"name":"Federal Reserve","domain":"www.federalreserve.gov","sourceType":"PRIMARY",
        "country":"US","language":"en","rssUrl":"%s","reliability":0.99,
        "enabled":true,"pollMinutes":15,"termsNotes":"Official press release metadata with attribution"}
        """;
    mvc.perform(
            put("/api/v1/admin/sources/fed")
                .with(auth)
                .contentType("application/json")
                .content(config.formatted("http://127.0.0.1/feed")))
        .andExpect(status().isBadRequest());
    mvc.perform(
            put("/api/v1/admin/sources/fed")
                .with(auth)
                .contentType("application/json")
                .content(config.formatted("https://www.federalreserve.gov/feeds/press_all.xml")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.enabled").value(true));
  }

  @Test
  void publicCannotReadDraftsOrPrivateArticles() throws Exception {
    mvc.perform(get("/api/v1/events/review-demo")).andExpect(status().isNotFound());
    mvc.perform(get("/api/v1/admin/articles")).andExpect(status().isUnauthorized());
    mvc.perform(post("/api/v1/admin/events/review-demo/approve"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void loginRejectsWrongPassword() throws Exception {
    mvc.perform(
            post("/api/v1/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void loginIssuesToken() throws Exception {
    mvc.perform(
            post("/api/v1/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"test-password-only\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isString());
  }

  @Test
  void nonAdminCannotReview() throws Exception {
    mvc.perform(
            get("/api/v1/admin/events")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ANALYST"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void reviewPublishesAndCorrectionReturnsToReview() throws Exception {
    var auth = jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"));
    mvc.perform(post("/api/v1/admin/events/review-demo/approve").with(auth))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PUBLISHED"));
    mvc.perform(get("/api/v1/events/review-demo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sources[0].url").isString());
    mvc.perform(
            post("/api/v1/admin/events/review-demo/edit")
                .with(auth)
                .contentType("application/json")
                .content("{\"summary\":\"Updated\"}"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/api/v1/admin/events/review-demo/edit")
                .with(auth)
                .contentType("application/json")
                .content(
                    "{\"summary\":\"Updated summary\",\"correctionNote\":\"Clarified source"
                        + " context\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REVIEW"));
    mvc.perform(get("/api/v1/events/review-demo")).andExpect(status().isNotFound());
    mvc.perform(get("/api/v1/admin/events/review-demo/audit").with(auth))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].action").value("EDIT"));
  }

  @Test
  void publicSearchAndRegistryWork() throws Exception {
    mvc.perform(get("/api/v1/search?q=Fed"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("fed-rate"));
    mvc.perform(get("/api/v1/sources"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(34));
  }
}
