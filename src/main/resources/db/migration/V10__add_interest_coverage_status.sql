-- Add interest_coverage_status column to quarterly_report.
-- Values: COVERED | NO_DEBT | OPERATING_LOSS (derived; null for pre-existing rows until next collection run).
ALTER TABLE quarterly_report ADD COLUMN interest_coverage_status VARCHAR(20);
