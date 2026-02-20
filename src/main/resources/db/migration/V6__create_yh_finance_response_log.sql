-- =============================================================================
-- V6: Create yh_finance_response_log table (Inbox Pattern)
-- =============================================================================
-- Purpose: Store raw API responses from Yahoo Finance for auditing
-- and reprocessing. Uses JSONB for the raw response payload.
-- =============================================================================

CREATE SEQUENCE IF NOT EXISTS yh_finance_response_log_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS yh_finance_response_log (
    id                  BIGINT PRIMARY KEY,
    ticker              VARCHAR(10) NOT NULL,
    request_timestamp   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    raw_response        JSONB NOT NULL,

    CONSTRAINT fk_yh_log_stock FOREIGN KEY (ticker) REFERENCES stock(ticker) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_yh_log_ticker
    ON yh_finance_response_log(ticker);

CREATE INDEX IF NOT EXISTS idx_yh_log_timestamp
    ON yh_finance_response_log(request_timestamp);
