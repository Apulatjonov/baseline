package uz.signal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, String> {
  java.util.Optional<Publication> findByEventIdAndChannel(String eventId, String channel);
}
