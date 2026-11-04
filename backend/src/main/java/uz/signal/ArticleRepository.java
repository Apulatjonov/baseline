package uz.signal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, String> {
  boolean existsByContentHash(String hash);

  java.util.List<Article> findByEventId(String eventId);
}
