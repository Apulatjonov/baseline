package uz.signal;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class RateLimitFilter extends org.springframework.web.filter.OncePerRequestFilter {
  private record Bucket(long minute, java.util.concurrent.atomic.AtomicInteger count) {}

  private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    long minute = System.currentTimeMillis() / 60000;
    boolean auth = req.getRequestURI().contains("/auth/");
    String key = req.getRemoteAddr() + ":" + auth;
    if (buckets.size() > 10000) buckets.entrySet().removeIf(e -> e.getValue().minute < minute);
    var b =
        buckets.compute(
            key,
            (k, v) ->
                v == null || v.minute != minute
                    ? new Bucket(minute, new java.util.concurrent.atomic.AtomicInteger())
                    : v);
    if (b.count.incrementAndGet() > (auth ? 10 : 180)) {
      res.setStatus(429);
      res.setHeader("Retry-After", "60");
      return;
    }
    chain.doFilter(req, res);
  }
}
