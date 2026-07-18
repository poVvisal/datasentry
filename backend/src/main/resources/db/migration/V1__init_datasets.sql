CREATE TABLE datasets (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL DEFAULT 'text/csv',
    size_bytes BIGINT NOT NULL CHECK (size_bytes > 0),
    storage_key VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_datasets_created_at ON datasets (created_at DESC);
