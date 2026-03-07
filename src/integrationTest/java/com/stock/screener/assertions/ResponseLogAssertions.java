package com.stock.screener.assertions;

import jakarta.persistence.EntityManager;

/**
 * Readable assertion helpers for verifying API response logs in integration tests.
 * Replaces raw SQL queries scattered across test methods.
 */
public final class ResponseLogAssertions {

    private final EntityManager em;

    private ResponseLogAssertions(EntityManager em) {
        this.em = em;
    }

    public static ResponseLogAssertions assertResponseLogs(EntityManager em) {
        return new ResponseLogAssertions(em);
    }

    // ── Alpha Vantage ──────────────────────────────────────────────────

    public ResponseLogAssertions hasAlphaVantageLogsForTicker(String ticker, long expectedMinCount) {
        long count = countAlphaVantageLogs(ticker);
        if (count < expectedMinCount) {
            throw new AssertionError(
                    "Expected at least %d AlphaVantage log(s) for ticker '%s', but found %d"
                            .formatted(expectedMinCount, ticker, count));
        }
        return this;
    }

    public ResponseLogAssertions hasAlphaVantageLogForFunction(String ticker, String functionName) {
        long count = ((Number) em
                .createNativeQuery("""
                        SELECT COUNT(*) FROM alpha_vantage_response_log
                        WHERE ticker = :ticker AND function_name = :fn
                        """)
                .setParameter("ticker", ticker)
                .setParameter("fn", functionName)
                .getSingleResult()).longValue();
        if (count == 0) {
            throw new AssertionError(
                    "Expected AlphaVantage log for ticker '%s' with function '%s', but none found"
                            .formatted(ticker, functionName));
        }
        return this;
    }

    public ResponseLogAssertions hasNoAlphaVantageLogsForTicker(String ticker) {
        long count = countAlphaVantageLogs(ticker);
        if (count > 0) {
            throw new AssertionError(
                    "Expected no AlphaVantage logs for ticker '%s', but found %d"
                            .formatted(ticker, count));
        }
        return this;
    }

    // ── Yahoo Finance ──────────────────────────────────────────────────

    public ResponseLogAssertions hasYhFinanceLogsForTicker(String ticker, long expectedMinCount) {
        long count = countYhFinanceLogs(ticker);
        if (count < expectedMinCount) {
            throw new AssertionError(
                    "Expected at least %d YhFinance log(s) for ticker '%s', but found %d"
                            .formatted(expectedMinCount, ticker, count));
        }
        return this;
    }

    public ResponseLogAssertions hasNoYhFinanceLogsForTicker(String ticker) {
        long count = countYhFinanceLogs(ticker);
        if (count > 0) {
            throw new AssertionError(
                    "Expected no YhFinance logs for ticker '%s', but found %d"
                            .formatted(ticker, count));
        }
        return this;
    }

    // ── Private helpers ────────────────────────────────────────────────

    private long countAlphaVantageLogs(String ticker) {
        return ((Number) em
                .createNativeQuery("SELECT COUNT(*) FROM alpha_vantage_response_log WHERE ticker = :ticker")
                .setParameter("ticker", ticker)
                .getSingleResult()).longValue();
    }

    private long countYhFinanceLogs(String ticker) {
        return ((Number) em
                .createNativeQuery("SELECT COUNT(*) FROM yh_finance_response_log WHERE ticker = :ticker")
                .setParameter("ticker", ticker)
                .getSingleResult()).longValue();
    }
}

