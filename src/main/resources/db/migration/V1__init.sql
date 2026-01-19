CREATE TABLE IF NOT EXISTS stock (
        ticker              VARCHAR(10) PRIMARY KEY,
        sector              VARCHAR(50),
        market_cap          DECIMAL(25, 2),
        current_price       DECIMAL(19, 4),
        ps_ratio            DECIMAL(10, 4),
        forward_pe_ratio    DECIMAL(10, 4),
        forward_peg_ratio   DECIMAL(10, 4),
        last_updated        TIMESTAMP
);


CREATE SEQUENCE IF NOT EXISTS monthly_report_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE IF NOT EXISTS monthly_report (
        id                      BIGINT PRIMARY KEY,
        stock_ticker            VARCHAR(10) NOT NULL,

        forward_revenue_growth  DECIMAL(10, 4), -- np. 0.1500 (15%)
        forward_eps_growth      DECIMAL(10, 4),
        target_price            DECIMAL(19, 4),
        ps_ratio                DECIMAL(10, 4),      -- snapshot to calculate median PS ratio
        forward_pe_ratio        DECIMAL(10, 4),
        forward_peg_ratio       DECIMAL(10, 4),
        -- analyst ratings
        strong_buy              INT,
        buy                     INT,
        hold                    INT,
        sell                    INT,
        strong_sell             INT,
        forecast_date           DATE DEFAULT CURRENT_DATE,

    CONSTRAINT fk_monthly_report_stock FOREIGN KEY (stock_ticker) REFERENCES stock(ticker) ON DELETE CASCADE,
    CONSTRAINT uq_monthly_report_stock_date UNIQUE (stock_ticker, forecast_date)
);


CREATE SEQUENCE IF NOT EXISTS quarterly_report_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE IF NOT EXISTS quarterly_report (
        id                      BIGINT PRIMARY KEY,
        stock_ticker            VARCHAR(10) NOT NULL,
        fiscal_date_ending      DATE NOT NULL,
        integrity_status        VARCHAR(30) NOT NULL,

        total_revenue           DECIMAL(25, 2),
        net_income              DECIMAL(25, 2),
        operating_cash_flow     DECIMAL(25, 2),
        total_debt              DECIMAL(25, 2),
        total_assets            DECIMAL(25, 2),
        quick_ratio             DECIMAL(10, 4),         -- (totalCurrentAssets - inventory) / totalCurrentLiabilities
        interest_coverage_ratio DECIMAL(10, 4),
        altman_z_score          DECIMAL(10, 4),         -- wzór na obliczenie w pliku data_collectedv2.md

        created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

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
    ON monthly_report(stock_ticker, forecast_date);

