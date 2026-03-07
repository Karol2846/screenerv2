-- Step 1: Add sector column to reports
ALTER TABLE monthly_report ADD COLUMN sector VARCHAR(30);
ALTER TABLE quarterly_report ADD COLUMN sector VARCHAR(30);

-- Step 2: Drop FK constraints
ALTER TABLE monthly_report DROP CONSTRAINT fk_monthly_report_stock;
ALTER TABLE quarterly_report DROP CONSTRAINT fk_report_stock;

-- Step 3: Drop stock table and its indexes (cascade)
DROP TABLE IF EXISTS stock CASCADE;
