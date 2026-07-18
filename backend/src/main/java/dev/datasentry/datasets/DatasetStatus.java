package dev.datasentry.datasets;

/** Lifecycle states for a dataset. Persisted as VARCHAR(20) via Flyway CHECK constraint. */
public enum DatasetStatus {
  UPLOADED,
  PROCESSING,
  READY,
  FAILED
}
