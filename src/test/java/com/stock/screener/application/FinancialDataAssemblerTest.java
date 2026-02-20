package com.stock.screener.application;

import com.stock.screener.application.port.out.alphavantage.*;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FinancialDataAssembler Tests")
class FinancialDataAssemblerTest {

    private FinancialDataAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new FinancialDataAssembler();
    }

    @Nested
    @DisplayName("Successful Assembly")
    class SuccessfulAssembly {

        @Test
        @DisplayName("Maps all fields correctly from matching reports")
        void testMapsAllFieldsCorrectly() {
            // Given
            var overview = createOverview("2800000000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "364980000000", "152987000000", "308030000000",
                    "176392000000", "56950000000", "4336000000", "7286000000");
            var incomeStatement = createIncomeStatement("2024-09-30",
                    "391035000000", "123216000000", "3200000000");

            // When
            FinancialDataSnapshot result = assembler.assemble(overview, balanceSheet, incomeStatement);

            // Then
            assertThat(result.marketCapitalization()).isEqualByComparingTo(new BigDecimal("2800000000000"));
            assertThat(result.totalAssets()).isEqualByComparingTo(new BigDecimal("364980000000"));
            assertThat(result.totalCurrentAssets()).isEqualByComparingTo(new BigDecimal("152987000000"));
            assertThat(result.totalLiabilities()).isEqualByComparingTo(new BigDecimal("308030000000"));
            assertThat(result.totalCurrentLiabilities()).isEqualByComparingTo(new BigDecimal("176392000000"));
            assertThat(result.totalShareholderEquity()).isEqualByComparingTo(new BigDecimal("56950000000"));
            assertThat(result.retainedEarnings()).isEqualByComparingTo(new BigDecimal("4336000000"));
            assertThat(result.inventory()).isEqualByComparingTo(new BigDecimal("7286000000"));
            assertThat(result.totalRevenue()).isEqualByComparingTo(new BigDecimal("391035000000"));
            assertThat(result.ebit()).isEqualByComparingTo(new BigDecimal("123216000000"));
            assertThat(result.interestExpense()).isEqualByComparingTo(new BigDecimal("3200000000"));
        }

        @Test
        @DisplayName("Matches income statement to latest balance sheet by fiscal date")
        void testDateMatching() {
            // Given: Balance sheet for 2024-09-30, income statements for 2024-09-30 and 2023-09-30
            var overview = createOverview("1000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "100", "50", "60", "30", "40", "10", "5");

            var is2024 = createIncomeStatementReport("2024-09-30", "500", "200", "15");
            var is2023 = createIncomeStatementReport("2023-09-30", "400", "150", "10");
            var incomeStatement = new IncomeStatementResponse("AAPL", List.of(is2024, is2023), List.of());

            // When
            FinancialDataSnapshot result = assembler.assemble(overview, balanceSheet, incomeStatement);

            // Then: Should pick 2024 income statement (matching balance sheet date)
            assertThat(result.totalRevenue()).isEqualByComparingTo(new BigDecimal("500"));
            assertThat(result.ebit()).isEqualByComparingTo(new BigDecimal("200"));
            assertThat(result.interestExpense()).isEqualByComparingTo(new BigDecimal("15"));
        }
    }

    @Nested
    @DisplayName("Null Safety")
    class NullSafety {

        @Test
        @DisplayName("Null inventory maps to BigDecimal.ZERO")
        void testNullInventoryMapsToZero() {
            // Given
            var overview = createOverview("1000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "100", "50", "60", "30", "40", "10", null);
            var incomeStatement = createIncomeStatement("2024-09-30", "500", "200", "15");

            // When
            FinancialDataSnapshot result = assembler.assemble(overview, balanceSheet, incomeStatement);

            // Then
            assertThat(result.inventory()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Null retainedEarnings maps to BigDecimal.ZERO")
        void testNullRetainedEarningsMapsToZero() {
            // Given
            var overview = createOverview("1000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "100", "50", "60", "30", "40", null, "5");
            var incomeStatement = createIncomeStatement("2024-09-30", "500", "200", "15");

            // When
            FinancialDataSnapshot result = assembler.assemble(overview, balanceSheet, incomeStatement);

            // Then
            assertThat(result.retainedEarnings()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Both null inventory and retainedEarnings map to BigDecimal.ZERO")
        void testBothNullFieldsMapToZero() {
            // Given
            var overview = createOverview("1000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "100", "50", "60", "30", "40", null, null);
            var incomeStatement = createIncomeStatement("2024-09-30", "500", "200", "15");

            // When
            FinancialDataSnapshot result = assembler.assemble(overview, balanceSheet, incomeStatement);

            // Then
            assertThat(result.inventory()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.retainedEarnings()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Throws when no annual balance sheet reports")
        void testThrowsWhenNoBalanceSheetReports() {
            // Given
            var overview = createOverview("1000000");
            var balanceSheet = new BalanceSheetResponse("AAPL", List.of(), List.of());
            var incomeStatement = createIncomeStatement("2024-09-30", "500", "200", "15");

            // When/Then
            assertThatThrownBy(() -> assembler.assemble(overview, balanceSheet, incomeStatement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No annual balance sheet reports available");
        }

        @Test
        @DisplayName("Throws when no matching income statement for balance sheet date")
        void testThrowsWhenNoMatchingIncomeStatement() {
            // Given: Balance sheet for 2024-09-30 but income statement only for 2023-09-30
            var overview = createOverview("1000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "100", "50", "60", "30", "40", "10", "5");
            var incomeStatement = createIncomeStatement("2023-09-30", "500", "200", "15");

            // When/Then
            assertThatThrownBy(() -> assembler.assemble(overview, balanceSheet, incomeStatement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No income statement found matching fiscal date: 2024-09-30");
        }

        @Test
        @DisplayName("Throws when no annual income statement reports")
        void testThrowsWhenNoIncomeStatementReports() {
            // Given
            var overview = createOverview("1000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "100", "50", "60", "30", "40", "10", "5");
            var incomeStatement = new IncomeStatementResponse("AAPL", List.of(), List.of());

            // When/Then
            assertThatThrownBy(() -> assembler.assemble(overview, balanceSheet, incomeStatement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No annual income statement reports available");
        }

        @Test
        @DisplayName("Throws when balance sheet annual reports list is null")
        void testThrowsWhenBalanceSheetReportsNull() {
            // Given
            var overview = createOverview("1000000");
            var balanceSheet = new BalanceSheetResponse("AAPL", null, List.of());
            var incomeStatement = createIncomeStatement("2024-09-30", "500", "200", "15");

            // When/Then
            assertThatThrownBy(() -> assembler.assemble(overview, balanceSheet, incomeStatement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No annual balance sheet reports available");
        }

        @Test
        @DisplayName("Throws when income statement annual reports list is null")
        void testThrowsWhenIncomeStatementReportsNull() {
            // Given
            var overview = createOverview("1000000");
            var balanceSheet = createBalanceSheet("2024-09-30",
                    "100", "50", "60", "30", "40", "10", "5");
            var incomeStatement = new IncomeStatementResponse("AAPL", null, List.of());

            // When/Then
            assertThatThrownBy(() -> assembler.assemble(overview, balanceSheet, incomeStatement))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No annual income statement reports available");
        }
    }

    // === Helper methods ===

    private static OverviewResponse createOverview(String marketCap) {
        return new OverviewResponse(
                "AAPL", "Common Stock", "Apple Inc", "NASDAQ", "USD", "USA",
                "TECHNOLOGY", "ELECTRONIC COMPUTERS",
                toBigDecimal(marketCap),
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null
        );
    }

    private static BalanceSheetResponse createBalanceSheet(String fiscalDate,
                                                           String totalAssets,
                                                           String totalCurrentAssets,
                                                           String totalLiabilities,
                                                           String totalCurrentLiabilities,
                                                           String equity,
                                                           String retainedEarnings,
                                                           String inventory) {
        var report = new BalanceSheetReport(
                fiscalDate, "USD",
                toBigDecimal(totalAssets), toBigDecimal(totalCurrentAssets), null,
                toBigDecimal(totalLiabilities), toBigDecimal(totalCurrentLiabilities), null,
                toBigDecimal(equity), toBigDecimal(retainedEarnings),
                null, null, null, toBigDecimal(inventory),
                null, null, null, null, null, null, null
        );
        return new BalanceSheetResponse("AAPL", List.of(report), List.of());
    }

    private static IncomeStatementResponse createIncomeStatement(String fiscalDate,
                                                                  String totalRevenue,
                                                                  String ebit,
                                                                  String interestExpense) {
        var report = createIncomeStatementReport(fiscalDate, totalRevenue, ebit, interestExpense);
        return new IncomeStatementResponse("AAPL", List.of(report), List.of());
    }

    private static IncomeStatementReport createIncomeStatementReport(String fiscalDate,
                                                                      String totalRevenue,
                                                                      String ebit,
                                                                      String interestExpense) {
        return new IncomeStatementReport(
                fiscalDate, "USD", null,
                toBigDecimal(totalRevenue), null, null, null, null, null, null, null,
                toBigDecimal(ebit), null, null, null,
                toBigDecimal(interestExpense), null, null, null
        );
    }

    private static BigDecimal toBigDecimal(String value) {
        return value != null ? new BigDecimal(value) : null;
    }
}
