-- Dodanie kolumny JSONB dla błędów kalkulacji w quarterly_report
ALTER TABLE quarterly_report
    ADD COLUMN IF NOT EXISTS calculation_errors JSONB DEFAULT '[]'::jsonb;

-- Indeks GIN dla efektywnego przeszukiwania błędów
CREATE INDEX IF NOT EXISTS idx_quarterly_report_errors
    ON quarterly_report USING GIN (calculation_errors);

COMMENT ON COLUMN quarterly_report.calculation_errors IS 'Lista błędów kalkulacji metryk w formacie JSONB - audit trail';

