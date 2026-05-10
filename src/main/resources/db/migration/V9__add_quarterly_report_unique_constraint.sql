ALTER TABLE quarterly_report ADD CONSTRAINT uq_quarterly_report_ticker_fiscal_date UNIQUE (ticker, fiscal_date_ending);
