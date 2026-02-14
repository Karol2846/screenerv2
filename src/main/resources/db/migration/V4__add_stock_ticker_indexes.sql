-- =============================================================================
-- V4: Add dedicated indexes on stock_ticker for reports
-- =============================================================================
-- Purpose: Optimize queries that access reports by stock_ticker without date filter.
-- This supports the new architecture where reports are queried independently
-- via their own repositories/Active Record, not navigated from Stock entity.
-- =============================================================================

-- Index for QuarterlyReport lookups by stock
CREATE INDEX IF NOT EXISTS idx_quarterly_report_stock_ticker
    ON quarterly_report(stock_ticker);

-- Index for MonthlyReport lookups by stock
CREATE INDEX IF NOT EXISTS idx_monthly_report_stock_ticker
    ON monthly_report(stock_ticker);

