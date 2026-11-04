package uz.signal;

import jakarta.persistence.*;

@Entity
@Table(name = "article_record")
public class Article {
  @Id public String id;

  @Column(columnDefinition = "text")
  public String sourceId;

  @Column(columnDefinition = "text")
  public String eventId;

  @Column(columnDefinition = "text")
  public String url;

  @Column(columnDefinition = "text")
  public String title;

  @Column(columnDefinition = "text")
  public String body;

  @Column(columnDefinition = "text")
  public String contentHash;

  @Column(columnDefinition = "text")
  public String publishedAt;

  @Column(columnDefinition = "text")
  public String retrievedAt;
}
