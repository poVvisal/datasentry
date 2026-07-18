package dev.datasentry.datasets;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/**
 * JPA entity for dataset metadata.
 *
 * <p>Schema is managed exclusively by Flyway (V1__create_datasets_table.sql). Never expose this
 * entity directly at the web layer – use {@link dev.datasentry.datasets.web.DatasetResponse}
 * instead.
 */
@Entity
@Table(name = "datasets")
@Getter
@Setter
@NoArgsConstructor
public class Dataset {

  @Id
  @UuidGenerator
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(name = "original_filename", nullable = false, length = 255)
  private String originalFilename;

  @Column(name = "content_type", nullable = false, length = 100)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private Long sizeBytes;

  @Column(name = "storage_key", nullable = false, length = 512)
  private String storageKey;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DatasetStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
    if (status == null) {
      status = DatasetStatus.UPLOADED;
    }
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
