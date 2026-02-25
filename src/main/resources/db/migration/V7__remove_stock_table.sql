-- =============================================================================
-- V7: Remove stock table, move sector to reports
-- =============================================================================

-- Step 1: Add sector column to reports
ALTER TABLE monthly_report ADD COLUMN sector VARCHAR(10);
ALTER TABLE quarterly_report ADD COLUMN sector VARCHAR(10);

-- Step 2: Backfill sector from stock table
UPDATE monthly_report mr SET sector = (SELECT s.sector FROM stock s WHERE s.ticker = mr.stock_ticker);
UPDATE quarterly_report qr SET sector = (SELECT s.sector FROM stock s WHERE s.ticker = qr.stock_ticker);

-- Step 3: Drop FK constraints
ALTER TABLE monthly_report DROP CONSTRAINT fk_monthly_report_stock;
ALTER TABLE quarterly_report DROP CONSTRAINT fk_report_stock;

-- Step 4: Drop stock table and its indexes (cascade)
DROP TABLE IF EXISTS stock CASCADE;
