package dev.datasentry.datasets.web;

import dev.datasentry.datasets.Dataset;
import dev.datasentry.datasets.DatasetStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO returned from all dataset endpoints.
 *
 * <p>Decoupled from the {@link dev.datasentry.datasets.Dataset} entity so the API contract stays
 * stable even if the entity changes internally.
 */
public record DatasetResponse(
    UUID id,
    String name,
    String originalFilename,
    String contentType,
    Long sizeBytes,
    String storageKey,
    DatasetStatus status,
    Instant createdAt,
    Instant updatedAt) {

  /** Factory method that maps an entity to this DTO. */
  public static DatasetResponse from(Dataset d) {
    return new DatasetResponse(
        d.getId(),
        d.getName(),
        d.getOriginalFilename(),
        d.getContentType(),
        d.getSizeBytes(),
        d.getStorageKey(),
        d.getStatus(),
        d.getCreatedAt(),
        d.getUpdatedAt());
  }
}
