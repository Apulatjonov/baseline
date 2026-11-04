package uz.signal;

import java.security.Principal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/sources")
public class SourceController {
  private final SourceRegistry registry;

  SourceController(SourceRegistry registry) {
    this.registry = registry;
  }

  @PutMapping("/{id}")
  Source configure(
      @PathVariable String id,
      @RequestBody SourceRegistry.Configuration config,
      Principal principal) {
    return registry.configure(id, config, principal.getName());
  }
}
