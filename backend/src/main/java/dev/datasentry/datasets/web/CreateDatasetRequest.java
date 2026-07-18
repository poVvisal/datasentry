package dev.datasentry.datasets.web;

import jakarta.validation.constraints.*;

/**
 * Request DTO for POST /api/v1/datasets.
 *
 * <p>Validated at the web boundary by Spring's {@code @Valid}. The JPA entity is never exposed here.
 */
public record CreateDatasetRequest(
    @NotBlank(message = "name is required")
        @Size(min = 3, max = 120, message = "name must be between 3 and 120 characters")
        String name,
    @NotBlank(message = "originalFilename is required") String originalFilename,
    @NotBlank(message = "contentType is required")
        @Pattern(
            regexp = "text/csv",
            message = "contentType must be text/csv")
        String contentType,
    @NotNull(message = "sizeBytes is required")
        @Positive(message = "sizeBytes must be a positive number")
        Long sizeBytes,
    @NotBlank(message = "storageKey is required") String storageKey) {}
