package uz.signal;

import jakarta.persistence.*;

@Entity
@Table(name = "event_record")
public class Event {
  @Id public String id;

  @Column(columnDefinition = "text")
  public String title;

  @Column(columnDefinition = "text")
  public String category;

  @Column(columnDefinition = "text")
  public String summary;

  @Column(columnDefinition = "text")
  public String whyItMatters;

  @Column(columnDefinition = "text")
  public String uzbekistanImpact;

  @Column(columnDefinition = "text")
  public String countries;

  @Column(columnDefinition = "text")
  public String entities;

  @Column(columnDefinition = "text")
  public String status;

  @Column(columnDefinition = "text")
  public String confidence;

  @Column(columnDefinition = "text")
  public String editorialLabel;

  public double importance;
  public double uzbekistanScore;

  @Column(columnDefinition = "text")
  public String publishedAt;

  @Column(columnDefinition = "text")
  public String updatedAt;

  public boolean demo;

  @Column(columnDefinition = "text")
  public String validationNotes;

  @Version public long version;

  public int synthesisAttempts;

  @Column(columnDefinition = "text")
  public String synthesisRetryAt;
}
