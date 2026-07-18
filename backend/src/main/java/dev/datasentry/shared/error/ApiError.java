package dev.datasentry.shared.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/**
 * Unified JSON error body returned for all 4xx/5xx responses.
 *
 * <pre>
 * {
 *   "timestamp": "2025-07-18T12:00:00Z",
 *   "status": 404,
 *   "error": "NOT_FOUND",
 *   "message": "Dataset not found: 550e8400-...",
 *   "path": "/api/v1/datasets/550e8400-...",
 *   "correlationId": "req-abc123",
 *   "fieldErrors": null
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String correlationId,
    List<FieldError> fieldErrors) {

  public record FieldError(String field, String message) {}
}
