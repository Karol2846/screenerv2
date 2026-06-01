package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.screener.collector.adapter.out.web.alphavantage.exception.ClientException;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.CashFlowResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.OverviewResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AlphaVantageResponseValidator")
class AlphaVantageResponseValidatorTest {

    private static final String TICKER = "AAPL";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ── Null response ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Null response")
    class NullResponse {

        @Test
        @DisplayName("validateOverview throws ClientException when response is null")
        void shouldThrowForNullOverview() {
            // Given
            OverviewResponse response = null;

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateOverview(TICKER, response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(TICKER);
        }

        @Test
        @DisplayName("validateReports throws ClientException when response is null")
        void shouldThrowForNullBalanceSheet() {
            // Given
            BalanceSheetResponse response = null;

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "BALANCE_SHEET", response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(TICKER);
        }
    }

    // ── AV error fields — Overview ─────────────────────────────────────────────

    @Nested
    @DisplayName("AV error fields — Overview")
    class AvErrorFieldsOverview {

        @Test
        @DisplayName("validateOverview throws ClientException with AV message when 'Information' field is set")
        void shouldThrowWhenInformationFieldPresent() {
            // Given
            String avMessage = "Thank you for using Alpha Vantage! Our standard API call frequency is 25 requests per day.";
            OverviewResponse response = overviewFromJson("{\"Information\": \"" + avMessage + "\"}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateOverview(TICKER, response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(avMessage)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("OVERVIEW");
        }

        @Test
        @DisplayName("validateOverview throws ClientException with AV message when 'Note' field is set")
        void shouldThrowWhenNoteFieldPresent() {
            // Given
            String avMessage = "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute.";
            OverviewResponse response = overviewFromJson("{\"Note\": \"" + avMessage + "\"}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateOverview(TICKER, response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(avMessage)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("OVERVIEW");
        }

        @Test
        @DisplayName("validateOverview throws ClientException with AV message when 'Error Message' field is set")
        void shouldThrowWhenErrorMessageFieldPresent() {
            // Given
            String avMessage = "Invalid API call. Please retry or visit the documentation for OVERVIEW.";
            OverviewResponse response = overviewFromJson("{\"Error Message\": \"" + avMessage + "\"}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateOverview(TICKER, response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(avMessage)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("OVERVIEW");
        }
    }

    // ── AV error fields — Reports ───────────────────────────────────────────────

    @Nested
    @DisplayName("AV error fields — Reports (Balance Sheet / Income Statement / Cash Flow)")
    class AvErrorFieldsReports {

        @Test
        @DisplayName("validateReports throws ClientException with AV message when 'Information' field is set on BalanceSheet")
        void shouldThrowWhenInformationPresentOnBalanceSheet() {
            // Given
            String avMessage = "Rate limit reached.";
            BalanceSheetResponse response = balanceSheetFromJson("{\"Information\": \"" + avMessage + "\"}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "BALANCE_SHEET", response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(avMessage)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("BALANCE_SHEET");
        }

        @Test
        @DisplayName("validateReports throws ClientException with AV message when 'Note' field is set on IncomeStatement")
        void shouldThrowWhenNotePresentOnIncomeStatement() {
            // Given
            String avMessage = "Thank you for using Alpha Vantage!";
            IncomeStatementResponse response = incomeStatementFromJson("{\"Note\": \"" + avMessage + "\"}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "INCOME_STATEMENT", response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(avMessage)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("INCOME_STATEMENT");
        }

        @Test
        @DisplayName("validateReports throws ClientException with AV message when 'Error Message' field is set on CashFlow")
        void shouldThrowWhenErrorMessagePresentOnCashFlow() {
            // Given
            String avMessage = "Invalid API call.";
            CashFlowResponse response = cashFlowFromJson("{\"Error Message\": \"" + avMessage + "\"}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "CASH_FLOW", response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(avMessage)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("CASH_FLOW");
        }
    }

    // ── Missing data guard — Overview ────────────────────────────────────────────

    @Nested
    @DisplayName("Missing data guard — Overview")
    class MissingDataOverview {

        @Test
        @DisplayName("validateOverview throws ClientException when symbol is null (all-null response)")
        void shouldThrowWhenSymbolIsNull() {
            // Given: AV returned a well-formed response but all data fields are null (e.g. no error field, but no symbol either)
            OverviewResponse response = overviewFromJson("{}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateOverview(TICKER, response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("OVERVIEW");
        }

        @Test
        @DisplayName("validateOverview throws ClientException when symbol is blank")
        void shouldThrowWhenSymbolIsBlank() {
            // Given
            OverviewResponse response = overviewFromJson("{\"Symbol\": \"   \"}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateOverview(TICKER, response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(TICKER);
        }

        @Test
        @DisplayName("validateOverview does not throw for a valid overview with symbol populated")
        void shouldNotThrowForValidOverview() {
            // Given
            OverviewResponse response = overviewFromJson("{\"Symbol\": \"AAPL\", \"Sector\": \"TECHNOLOGY\"}");

            // When / Then
            assertThatNoException().isThrownBy(() -> AlphaVantageResponseValidator.validateOverview(TICKER, response));
        }
    }

    // ── Missing data guard — Reports ─────────────────────────────────────────────

    @Nested
    @DisplayName("Missing data guard — Reports")
    class MissingDataReports {

        @Test
        @DisplayName("validateReports throws ClientException when both annualReports and quarterlyReports are null")
        void shouldThrowWhenBothReportListsNull() {
            // Given
            BalanceSheetResponse response = balanceSheetFromJson("{}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "BALANCE_SHEET", response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(TICKER)
                    .hasMessageContaining("BALANCE_SHEET");
        }

        @Test
        @DisplayName("validateReports throws ClientException when both report lists are empty")
        void shouldThrowWhenBothReportListsEmpty() {
            // Given
            BalanceSheetResponse response = balanceSheetFromJson("{\"annualReports\": [], \"quarterlyReports\": []}");

            // When / Then
            assertThatThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "BALANCE_SHEET", response))
                    .isInstanceOf(ClientException.class)
                    .hasMessageContaining(TICKER);
        }

        @Test
        @DisplayName("validateReports does not throw when at least quarterlyReports is non-empty")
        void shouldNotThrowWhenQuarterlyReportsPresent() {
            // Given
            BalanceSheetResponse response = balanceSheetFromJson(
                    "{\"symbol\": \"AAPL\", \"annualReports\": [], \"quarterlyReports\": [{\"fiscalDateEnding\": \"2024-09-30\"}]}");

            // When / Then
            assertThatNoException().isThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "BALANCE_SHEET", response));
        }

        @Test
        @DisplayName("validateReports does not throw when only annualReports is non-empty")
        void shouldNotThrowWhenAnnualReportsPresent() {
            // Given
            BalanceSheetResponse response = balanceSheetFromJson(
                    "{\"symbol\": \"AAPL\", \"annualReports\": [{\"fiscalDateEnding\": \"2024-09-30\"}]}");

            // When / Then
            assertThatNoException().isThrownBy(() -> AlphaVantageResponseValidator.validateReports(TICKER, "BALANCE_SHEET", response));
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private static OverviewResponse overviewFromJson(String json) {
        try {
            return MAPPER.readValue(json, OverviewResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OverviewResponse from JSON: " + json, e);
        }
    }

    private static BalanceSheetResponse balanceSheetFromJson(String json) {
        try {
            return MAPPER.readValue(json, BalanceSheetResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse BalanceSheetResponse from JSON: " + json, e);
        }
    }

    private static IncomeStatementResponse incomeStatementFromJson(String json) {
        try {
            return MAPPER.readValue(json, IncomeStatementResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse IncomeStatementResponse from JSON: " + json, e);
        }
    }

    private static CashFlowResponse cashFlowFromJson(String json) {
        try {
            return MAPPER.readValue(json, CashFlowResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CashFlowResponse from JSON: " + json, e);
        }
    }
}
