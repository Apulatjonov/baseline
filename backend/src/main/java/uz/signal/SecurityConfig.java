package uz.signal;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Configuration
class SecurityConfig {
  @Bean
  SecretKeySpec key(
      @Value("${signal.jwt-secret}") String secret, @Value("${signal.demo}") boolean demo) {
    if (secret.isBlank()) {
      if (!demo) throw new IllegalStateException("JWT_SECRET is required outside demo mode");
      byte[] bytes = new byte[32];
      new SecureRandom().nextBytes(bytes);
      return new SecretKeySpec(bytes, "HmacSHA256");
    }
    if (secret.getBytes(StandardCharsets.UTF_8).length < 32)
      throw new IllegalStateException("JWT_SECRET must contain at least 32 bytes");
    return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }

  @Bean
  JwtDecoder decoder(SecretKeySpec key) {
    return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  }

  @Bean
  JwtEncoder encoder(SecretKeySpec key) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(key));
  }

  @Bean
  SecurityFilterChain chain(HttpSecurity http) throws Exception {
    return http.csrf(c -> c.disable())
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers("/api/v1/auth/login", "/actuator/health")
                    .permitAll()
                    .requestMatchers("/api/v1/admin/**", "/actuator/**")
                    .hasAuthority("SCOPE_ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/**")
                    .permitAll()
                    .anyRequest()
                    .denyAll())
        .oauth2ResourceServer(o -> o.jwt(j -> {}))
        .build();
  }
}

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
  private final JwtEncoder encoder;
  private final String user, password;

  AuthController(
      JwtEncoder encoder,
      @Value("${signal.admin-user}") String user,
      @Value("${signal.admin-password}") String password) {
    this.encoder = encoder;
    this.user = user;
    this.password = password;
  }

  record Login(String username, String password) {}

  @PostMapping("/login")
  Map<String, String> login(@RequestBody Login request) {
    if (password.isBlank())
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "Configure ADMIN_PASSWORD to enable editorial access");
    if (!user.equals(request.username())
        || request.password() == null
        || !MessageDigest.isEqual(
            password.getBytes(StandardCharsets.UTF_8),
            request.password().getBytes(StandardCharsets.UTF_8)))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    var now = Instant.now();
    var claims =
        JwtClaimsSet.builder()
            .subject(user)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .claim("scope", "ADMIN")
            .build();
    return Map.of(
        "token",
        encoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue());
  }
}
