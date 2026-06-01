package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.stock.screener.collector.adapter.out.web.alphavantage.exception.ClientException;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.AlphaVantageResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.CashFlowResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.OverviewResponse;

/**
 * Validates Alpha Vantage API responses before they are processed or persisted.
 * <p>
 * AV signals quota/error conditions via HTTP 200 with a JSON body containing
 * {@code "Information"}, {@code "Note"}, or {@code "Error Message"} instead of data.
 * Without explicit validation these silently deserialise to all-null DTOs.
 * </p>
 */
final class AlphaVantageResponseValidator {

    private static final String OVERVIEW = "OVERVIEW";

    private AlphaVantageResponseValidator() {
    }

    /**
     * Validates an OVERVIEW response.
     *
     * @throws ClientException if the response is null, contains an AV error field,
     *                         or the {@code symbol} field is blank (indicating empty data).
     */
    static void validateOverview(String ticker, OverviewResponse response) {
        requireNonNull(ticker, OVERVIEW, response);
        checkErrorFields(ticker, OVERVIEW, response);
        if (response.symbol() == null || response.symbol().isBlank()) {
            throw new ClientException(
                    "Empty OVERVIEW response from Alpha Vantage for ticker: %s — symbol field is blank", ticker);
        }
    }

    /**
     * Validates a report-type response (BALANCE_SHEET / INCOME_STATEMENT / CASH_FLOW).
     *
     * @throws ClientException if the response is null, contains an AV error field,
     *                         or both report lists are null/empty.
     */
    static void validateReports(String ticker, String function, AlphaVantageResponse response) {
        requireNonNull(ticker, function, response);
        checkErrorFields(ticker, function, response);
        checkReportsNonEmpty(ticker, function, response);
    }

    // ── private helpers ────────────────────────────────────────────────────────

    private static void requireNonNull(String ticker, String function, AlphaVantageResponse response) {
        if (response == null) {
            throw new ClientException(
                    "Null response from Alpha Vantage for ticker: %s [%s]", ticker, function);
        }
    }

    private static void checkErrorFields(String ticker, String function, AlphaVantageResponse response) {
        String errorMessage = firstNonBlank(response.information(), response.note(), response.errorMessage());
        if (errorMessage != null) {
            throw new ClientException(
                    "Alpha Vantage error for ticker: %s [%s]: %s", ticker, function, errorMessage);
        }
    }

    private static void checkReportsNonEmpty(String ticker, String function, AlphaVantageResponse response) {
        boolean hasReports = switch (response) {
            case BalanceSheetResponse r ->
                    (r.annualReports() != null && !r.annualReports().isEmpty())
                    || (r.quarterlyReports() != null && !r.quarterlyReports().isEmpty());
            case IncomeStatementResponse r ->
                    (r.annualReports() != null && !r.annualReports().isEmpty())
                    || (r.quarterlyReports() != null && !r.quarterlyReports().isEmpty());
            case CashFlowResponse r ->
                    (r.annualReports() != null && !r.annualReports().isEmpty())
                    || (r.quarterlyReports() != null && !r.quarterlyReports().isEmpty());
            default -> true; // unknown subtype — let it through
        };
        if (!hasReports) {
            throw new ClientException(
                    "Empty report lists from Alpha Vantage for ticker: %s [%s] — both annualReports and quarterlyReports are null/empty",
                    ticker, function);
        }
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }
}
