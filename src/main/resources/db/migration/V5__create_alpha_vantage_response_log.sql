-- =============================================================================
-- V5: Create alpha_vantage_response_log table (Inbox Pattern)
-- =============================================================================
-- Purpose: Store raw API responses from Alpha Vantage for auditing
-- and reprocessing. Uses JSONB for the raw response payload.
-- =============================================================================

CREATE SEQUENCE IF NOT EXISTS alpha_vantage_response_log_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS alpha_vantage_response_log (
    id                  BIGINT PRIMARY KEY,
    ticker              VARCHAR(10) NOT NULL,
    function_name       VARCHAR(50) NOT NULL,
    request_timestamp   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    raw_response        JSONB NOT NULL,

    CONSTRAINT fk_av_log_stock FOREIGN KEY (ticker) REFERENCES stock(ticker) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_av_log_ticker
    ON alpha_vantage_response_log(ticker);

CREATE INDEX IF NOT EXISTS idx_av_log_function
    ON alpha_vantage_response_log(ticker, function_name);

CREATE INDEX IF NOT EXISTS idx_av_log_timestamp
    ON alpha_vantage_response_log(request_timestamp);
