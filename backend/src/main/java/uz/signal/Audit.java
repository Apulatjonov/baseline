package uz.signal;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_record")
public class Audit {
  @Id public String id;

  @Column(columnDefinition = "text")
  public String eventId;

  @Column(columnDefinition = "text")
  public String action;

  @Column(columnDefinition = "text")
  public String actor;

  @Column(columnDefinition = "text")
  public String payload;

  @Column(columnDefinition = "text")
  public String createdAt;
}
