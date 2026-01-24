-- Dodanie kolumn dla MonthlyReport: calculation_errors, integrity_status, upside_potential

-- Kolumna JSONB dla błędów kalkulacji
ALTER TABLE monthly_report
    ADD COLUMN IF NOT EXISTS calculation_errors JSONB DEFAULT '[]'::jsonb;

-- Indeks GIN dla efektywnego przeszukiwania błędów
CREATE INDEX IF NOT EXISTS idx_monthly_report_errors
    ON monthly_report USING GIN (calculation_errors);

-- Status integralności raportu
ALTER TABLE monthly_report
    ADD COLUMN IF NOT EXISTS integrity_status VARCHAR(50);

-- Kolumna dla upside potential (nowa metryka)
ALTER TABLE monthly_report
    ADD COLUMN IF NOT EXISTS upside_potential NUMERIC;

-- Kolumna updated_at
ALTER TABLE monthly_report
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

COMMENT ON COLUMN monthly_report.calculation_errors IS 'Lista błędów kalkulacji metryk w formacie JSONB - audit trail';
COMMENT ON COLUMN monthly_report.integrity_status IS 'Status integralności danych: COMPLETE, STALE_MISSING_DATA, etc.';
COMMENT ON COLUMN monthly_report.upside_potential IS 'Procentowy potencjał wzrostu względem target price';

