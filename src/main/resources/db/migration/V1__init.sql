CREATE TABLE IF NOT EXISTS stock  (
        ticker VARCHAR(10) NOT NULL PRIMARY KEY,
        sector VARCHAR(50),

        current_price DECIMAL(19, 4),
        market_cap DECIMAL(25, 2), -- Market capy są ogromne (np. 3T USD)
        pe_ratio DECIMAL(10, 4),
        forward_pe_ratio DECIMAL(10, 4),
        ps_ratio DECIMAL(10, 4),
        peg_ratio DECIMAL(10, 4),
        last_updated TIMESTAMP
);


-- Tabela FORWARD_ESTIMATES
CREATE TABLE IF NOT EXISTS forward_estimates (
        id BIGSERIAL PRIMARY KEY,
        stock_ticker VARCHAR(10) NOT NULL,

        forward_revenue_growth_2y DECIMAL(10, 4), -- np. 0.1500 (15%)
        forward_eps_growth_2y DECIMAL(10, 4),
        target_price DECIMAL(19, 4),
        forecast_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_estimates_stock FOREIGN KEY (stock_ticker) REFERENCES stock(ticker) ON DELETE CASCADE,
);


-- Tabela QUARTERLY_REPORT
CREATE TABLE quarterly_report (
        id BIGSERIAL PRIMARY KEY,
        stock_ticker VARCHAR(10) NOT NULL,
        fiscal_date_ending DATE NOT NULL,

        -- Status raportu (enum)
        integrity_status VARCHAR(30) NOT NULL,

        -- Income Statement
        total_revenue DECIMAL(25, 2),
        net_income DECIMAL(25, 2),
        ebit DECIMAL(25, 2),
        interest_expense DECIMAL(25, 2),

        -- Balance Sheet (Kluczowe dla Z-Score)
        total_assets DECIMAL(25, 2),
        total_current_assets DECIMAL(25, 2),
        total_liabilities DECIMAL(25, 2),
        total_current_liabilities DECIMAL(25, 2),
        retained_earnings DECIMAL(25, 2),

        -- Cash Flow
        operating_cash_flow DECIMAL(25, 2),

        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

CONSTRAINT fk_report_stock FOREIGN KEY (stock_ticker) REFERENCES stock(ticker) ON DELETE CASCADE,
CONSTRAINT uq_report_stock_date UNIQUE (stock_ticker, fiscal_date_ending)
);


CREATE INDEX idx_stock_sector
    ON stock(sector);

CREATE INDEX idx_report_status
    ON quarterly_report(integrity_status);

CREATE INDEX idx_report_date
    ON quarterly_report(stock_ticker, fiscal_date_ending);

CREATE INDEX idx_estimates_date
    ON forward_estimates(stock_ticker, forecast_date);

