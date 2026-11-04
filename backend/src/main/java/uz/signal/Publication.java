package uz.signal;

import jakarta.persistence.*;

@Entity
@Table(name = "publication_record")
public class Publication {
  @Id public String id;

  @Column(columnDefinition = "text")
  public String eventId;

  @Column(columnDefinition = "text")
  public String channel;

  @Column(columnDefinition = "text")
  public String status;

  @Column(columnDefinition = "text")
  public String externalId;

  @Column(columnDefinition = "text")
  public String body;

  @Column(columnDefinition = "text")
  public String createdAt;
}
