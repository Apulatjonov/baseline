package uz.signal;

import jakarta.persistence.*;

@Entity
@Table(name = "source_record")
public class Source {
  @Id public String id;

  @Column(columnDefinition = "text")
  public String name;

  @Column(columnDefinition = "text")
  public String domain;

  @Column(columnDefinition = "text")
  public String sourceType;

  @Column(columnDefinition = "text")
  public String country;

  @Column(columnDefinition = "text")
  public String language;

  @Column(columnDefinition = "text")
  public String rssUrl;

  public double reliability;
  public boolean enabled;
  public int pollMinutes;

  @Column(columnDefinition = "text")
  public String termsNotes;

  @Column(columnDefinition = "text")
  public String lastSuccess;

  @Column(columnDefinition = "text")
  public String lastFailure;

  public long articlesCollected;

  @Column(columnDefinition = "text")
  public String lastAttempt;
}
