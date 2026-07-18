-- V1: Create datasets table
-- Owned exclusively by Flyway. Never alter via Hibernate ddl-auto.

CREATE TABLE IF NOT EXISTS datasets
(
    id                UUID                     NOT NULL,
    name              VARCHAR(120)             NOT NULL,
    original_filename VARCHAR(255)             NOT NULL,
    content_type      VARCHAR(100)             NOT NULL,
    size_bytes        BIGINT                   NOT NULL,
    storage_key       VARCHAR(512)             NOT NULL,
    status            VARCHAR(20)              NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_datasets PRIMARY KEY (id),
    CONSTRAINT chk_datasets_size_bytes_positive CHECK (size_bytes > 0),
    CONSTRAINT chk_datasets_status CHECK (status IN ('UPLOADED', 'PROCESSING', 'READY', 'FAILED')),
    CONSTRAINT chk_datasets_name_length CHECK (char_length(name) >= 3)
);

-- Index for newest-first listing (GET /api/v1/datasets default order)
CREATE INDEX IF NOT EXISTS idx_datasets_created_at_desc ON datasets (created_at DESC);

COMMENT ON TABLE datasets IS 'Stores metadata for user-uploaded CSV files. Storage object managed by MinIO (future).';
COMMENT ON COLUMN datasets.storage_key IS 'Future MinIO object key. Placeholder on Day 1.';
COMMENT ON COLUMN datasets.status IS 'Lifecycle: UPLOADED → PROCESSING → READY | FAILED';
