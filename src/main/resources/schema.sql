-- Enable PostgreSQL UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Partitioned Core Table Definition for Transfers
CREATE TABLE IF NOT EXISTS transfers (
    id UUID DEFAULT uuid_generate_v4(),
    origin_account VARCHAR(50) NOT NULL,
    destination_account VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             PRIMARY KEY (id, created_at)
    ) PARTITION BY RANGE (created_at);

-- Monthly Partitions Setup
CREATE TABLE IF NOT EXISTS transfers_2026_07 PARTITION OF transfers
    FOR VALUES FROM ('2026-07-01 00:00:00+00') TO ('2026-08-01 00:00:00+00');

CREATE TABLE IF NOT EXISTS transfers_2026_08 PARTITION OF transfers
    FOR VALUES FROM ('2026-08-01 00:00:00+00') TO ('2026-09-01 00:00:00+00');

-- GIN Index for High-Performance Full-Text Search on Transfer Descriptions
CREATE INDEX IF NOT EXISTS idx_transfers_description_gin
    ON transfers USING GIN (to_tsvector('english', COALESCE(description, '')));