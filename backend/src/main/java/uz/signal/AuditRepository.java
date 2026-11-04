package uz.signal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, String> {
  java.util.List<Audit> findByEventIdOrderByCreatedAtDesc(String eventId);
}
