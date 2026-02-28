package com.stock.screener.collector.application.service;

import com.stock.screener.collector.domain.valueobject.AnalystRatings;
import com.stock.screener.collector.domain.valueobject.snapshot.FinancialDataSnapshot;
import com.stock.screener.collector.domain.valueobject.snapshot.MarketDataSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import com.stock.screener.collector.application.port.out.fixtures.RawBalanceSheetFixture;
import com.stock.screener.collector.application.port.out.fixtures.RawIncomeStatementFixture;

import static com.stock.screener.collector.application.port.out.fixtures.RawBalanceSheetFixture.aRawBalanceSheet;
import static com.stock.screener.collector.application.port.out.fixtures.RawCashFlowFixture.aRawCashFlow;
import static com.stock.screener.collector.application.port.out.fixtures.RawIncomeStatementFixture.aRawIncomeStatement;
import static com.stock.screener.collector.application.port.out.fixtures.RawOverviewFixture.aRawOverview;
import static com.stock.screener.collector.application.port.out.fixtures.YhFinanceResponseFixture.aYhFinanceResponse;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StockDataMapper Tests")
class StockDataMapperTest {

    private final StockDataMapper mapper = new StockDataMapper();

    // ==================== toMarketDataSnapshot ====================

    @Nested
    @DisplayName("toMarketDataSnapshot")
    class ToMarketDataSnapshotTests {

        @Test
        @DisplayName("Maps all fields from both sources when both are present")
        void mapsBothSources() {
            // Given
            var overview = aRawOverview()
                    .withMarketCapitalization("2800000000000")
                    .withRevenueTTM("391035000000")
                    .withForwardPE("25.00")
                    .withAnalystTargetPrice("235.00")
                    .build();
            var yhResponse = aYhFinanceResponse()
                    .withCurrentPrice("175.50")
                    .withForwardEpsGrowth("0.15")
                    .withForwardRevenueGrowth("0.12")
                    .build();

            // When
            MarketDataSnapshot result = mapper.toMarketDataSnapshot(overview, yhResponse);

            // Then — AlphaVantage fields
            assertThat(result.marketCap()).isEqualByComparingTo(new BigDecimal("2800000000000"));
            assertThat(result.revenueTTM()).isEqualByComparingTo(new BigDecimal("391035000000"));
            assertThat(result.forwardPeRatio()).isEqualByComparingTo(new BigDecimal("25.00"));
            assertThat(result.targetPrice()).isEqualByComparingTo(new BigDecimal("235.00"));

            // Then — Yahoo Finance fields
            assertThat(result.currentPrice()).isEqualByComparingTo(new BigDecimal("175.50"));
            assertThat(result.forwardEpsGrowth()).isEqualByComparingTo(new BigDecimal("0.15"));
            assertThat(result.forwardRevenueGrowth()).isEqualByComparingTo(new BigDecimal("0.12"));
            assertThat(result.analystRatings().strongBuy()).isEqualTo(11);
            assertThat(result.analystRatings().buy()).isEqualTo(51);
            assertThat(result.analystRatings().hold()).isEqualTo(5);
            assertThat(result.analystRatings().sell()).isEqualTo(0);
            assertThat(result.analystRatings().strongSell()).isEqualTo(0);
        }

        @Test
        @DisplayName("Maps overview-only when yhResponse is null")
        void mapsOverviewOnly() {
            // Given
            var overview = aRawOverview().build();

            // When
            MarketDataSnapshot result = mapper.toMarketDataSnapshot(overview, null);

            // Then — AlphaVantage fields match fixture defaults
            assertThat(result.marketCap()).isEqualByComparingTo(new BigDecimal("1661943284000"));
            assertThat(result.revenueTTM()).isEqualByComparingTo(new BigDecimal("200965997000"));
            assertThat(result.forwardPeRatio()).isEqualByComparingTo(new BigDecimal("21.28"));
            assertThat(result.targetPrice()).isEqualByComparingTo(new BigDecimal("861.42"));

            // Then — Yahoo Finance fields null
            assertThat(result.currentPrice()).isNull();
            assertThat(result.forwardEpsGrowth()).isNull();
            assertThat(result.forwardRevenueGrowth()).isNull();
            assertThat(result.analystRatings()).isNull();
        }

        @Test
        @DisplayName("Maps yhResponse-only when overview is null")
        void mapsYhResponseOnly() {
            // Given
            var yhResponse = aYhFinanceResponse().build();

            // When
            MarketDataSnapshot result = mapper.toMarketDataSnapshot(null, yhResponse);

            // Then — AlphaVantage fields null
            assertThat(result.marketCap()).isNull();
            assertThat(result.revenueTTM()).isNull();
            assertThat(result.forwardPeRatio()).isNull();
            assertThat(result.targetPrice()).isNull();

            // Then — Yahoo Finance fields match fixture defaults
            assertThat(result.currentPrice()).isEqualByComparingTo(new BigDecimal("648.18"));
            assertThat(result.forwardEpsGrowth()).isEqualByComparingTo(new BigDecimal("0.1866"));
            assertThat(result.forwardRevenueGrowth()).isEqualByComparingTo(new BigDecimal("0.177"));
            assertThat(result.analystRatings().strongBuy()).isEqualTo(11);
            assertThat(result.analystRatings().buy()).isEqualTo(51);
            assertThat(result.analystRatings().hold()).isEqualTo(5);
            assertThat(result.analystRatings().sell()).isEqualTo(0);
            assertThat(result.analystRatings().strongSell()).isEqualTo(0);
        }

        @Test
        @DisplayName("Returns empty snapshot when both sources are null")
        void bothNull() {
            // When
            MarketDataSnapshot result = mapper.toMarketDataSnapshot(null, null);

            // Then — all fields null
            assertThat(result.marketCap()).isNull();
            assertThat(result.revenueTTM()).isNull();
            assertThat(result.forwardPeRatio()).isNull();
            assertThat(result.targetPrice()).isNull();
            assertThat(result.currentPrice()).isNull();
            assertThat(result.forwardEpsGrowth()).isNull();
            assertThat(result.forwardRevenueGrowth()).isNull();
            assertThat(result.analystRatings()).isNull();
        }

        @Test
        @DisplayName("Maps analyst ratings correctly from yhResponse")
        void mapsAnalystRatings() {
            // Given
            var ratings = AnalystRatings.builder()
                    .strongBuy(10).buy(15).hold(5).sell(2).strongSell(1).build();
            var yhResponse = aYhFinanceResponse().withAnalystRatings(ratings).build();

            // When
            MarketDataSnapshot result = mapper.toMarketDataSnapshot(null, yhResponse);

            // Then
            assertThat(result.analystRatings().strongBuy()).isEqualTo(10);
            assertThat(result.analystRatings().buy()).isEqualTo(15);
            assertThat(result.analystRatings().hold()).isEqualTo(5);
            assertThat(result.analystRatings().sell()).isEqualTo(2);
            assertThat(result.analystRatings().strongSell()).isEqualTo(1);
        }
    }

    // ==================== toFinancialDataSnapshot ====================

    @Nested
    @DisplayName("toFinancialDataSnapshot")
    class ToFinancialDataSnapshotTests {

        @Test
        @DisplayName("Maps all fields from all three sources")
        void mapsAllSources() {
            // Given
            var balanceSheet = aRawBalanceSheet().build();
            var incomeStatement = aRawIncomeStatement().build();
            var cashFlow = aRawCashFlow().build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, incomeStatement, cashFlow);

            // Then — Balance sheet fields
            assertThat(result.totalCurrentAssets()).isEqualByComparingTo(new BigDecimal("108722000000"));
            assertThat(result.totalCurrentLiabilities()).isEqualByComparingTo(new BigDecimal("41836000000"));
            assertThat(result.totalAssets()).isEqualByComparingTo(new BigDecimal("366021000000"));
            assertThat(result.totalLiabilities()).isEqualByComparingTo(new BigDecimal("148778000000"));
            assertThat(result.totalShareholderEquity()).isEqualByComparingTo(new BigDecimal("217243000000"));
            assertThat(result.inventory()).isNull();

            // Then — Income statement fields
            assertThat(result.interestExpense()).isEqualByComparingTo(new BigDecimal("708000000"));
            assertThat(result.totalRevenue()).isEqualByComparingTo(new BigDecimal("59894000000"));
            assertThat(result.netIncome()).isEqualByComparingTo(new BigDecimal("22768000000"));

            // Then — Derived fields
            assertThat(result.retainedEarnings()).isEqualByComparingTo(new BigDecimal("121179000000"));
            assertThat(result.ebit()).isEqualByComparingTo(new BigDecimal("26061000000"));
            assertThat(result.totalDebt()).isEqualByComparingTo(new BigDecimal("83897000000"));
            assertThat(result.revenueTTM()).isEqualByComparingTo(new BigDecimal("59894000000"));

            // Then — Cash flow fields
            assertThat(result.operatingCashFlow()).isEqualByComparingTo(new BigDecimal("36214000000"));
        }

        @Test
        @DisplayName("Returns snapshot with nulls when all sources are null")
        void allNull() {
            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(null, null, null);

            // Then
            assertThat(result.totalCurrentAssets()).isNull();
            assertThat(result.totalCurrentLiabilities()).isNull();
            assertThat(result.totalAssets()).isNull();
            assertThat(result.totalLiabilities()).isNull();
            assertThat(result.totalShareholderEquity()).isNull();
            assertThat(result.inventory()).isNull();
            assertThat(result.interestExpense()).isNull();
            assertThat(result.totalRevenue()).isNull();
            assertThat(result.netIncome()).isNull();
            assertThat(result.retainedEarnings()).isNull();
            assertThat(result.ebit()).isNull();
            assertThat(result.totalDebt()).isNull();
            assertThat(result.revenueTTM()).isNull();
            assertThat(result.operatingCashFlow()).isNull();
        }

        @Test
        @DisplayName("Handles null quarterly reports gracefully")
        void handlesNullQuarterlyReports() {
            // Given
            var balanceSheet = aRawBalanceSheet().withNullQuarterlyReports().build();
            var incomeStatement = aRawIncomeStatement().withNullQuarterlyReports().build();
            var cashFlow = aRawCashFlow().withNullQuarterlyReports().build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, incomeStatement, cashFlow);

            // Then — all report-derived fields null
            assertThat(result.totalCurrentAssets()).isNull();
            assertThat(result.totalCurrentLiabilities()).isNull();
            assertThat(result.totalAssets()).isNull();
            assertThat(result.totalLiabilities()).isNull();
            assertThat(result.totalShareholderEquity()).isNull();
            assertThat(result.inventory()).isNull();
            assertThat(result.interestExpense()).isNull();
            assertThat(result.totalRevenue()).isNull();
            assertThat(result.netIncome()).isNull();
            assertThat(result.retainedEarnings()).isNull();
            assertThat(result.ebit()).isNull();
            assertThat(result.totalDebt()).isNull();
            assertThat(result.revenueTTM()).isNull();
            assertThat(result.operatingCashFlow()).isNull();
        }

        @Test
        @DisplayName("Handles empty quarterly reports gracefully")
        void handlesEmptyQuarterlyReports() {
            // Given
            var balanceSheet = aRawBalanceSheet()
                    .withQuarterlyReports(List.of()).build();
            var incomeStatement = aRawIncomeStatement()
                    .withQuarterlyReports(List.of()).build();
            var cashFlow = aRawCashFlow()
                    .withQuarterlyReports(List.of()).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, incomeStatement, cashFlow);

            // Then
            assertThat(result.totalCurrentAssets()).isNull();
            assertThat(result.totalCurrentLiabilities()).isNull();
            assertThat(result.totalAssets()).isNull();
            assertThat(result.totalLiabilities()).isNull();
            assertThat(result.totalShareholderEquity()).isNull();
            assertThat(result.inventory()).isNull();
            assertThat(result.interestExpense()).isNull();
            assertThat(result.totalRevenue()).isNull();
            assertThat(result.netIncome()).isNull();
            assertThat(result.retainedEarnings()).isNull();
            assertThat(result.ebit()).isNull();
            assertThat(result.totalDebt()).isNull();
            assertThat(result.revenueTTM()).isNull();
            assertThat(result.operatingCashFlow()).isNull();
        }
    }

    // ==================== resolveRetainedEarnings ====================

    @Nested
    @DisplayName("resolveRetainedEarnings")
    class ResolveRetainedEarningsTests {

        @Test
        @DisplayName("Uses retainedEarnings directly when present")
        void usesDirectValue() {
            // Given
            var report = RawBalanceSheetFixture.aReport()
                    .withRetainedEarnings("4336000000")
                    .build();
            var balanceSheet = aRawBalanceSheet().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, null, null);

            // Then
            assertThat(result.retainedEarnings())
                    .isEqualByComparingTo(new BigDecimal("4336000000"));
        }

        @Test
        @DisplayName("Fallback: totalShareholderEquity - (commonStock + additionalPaidInCapital)")
        void fallbackCalculation() {
            // Given — retainedEarnings is null, but equity/commonStock/APIC are present
            var report = RawBalanceSheetFixture.aReport()
                    .withNullRetainedEarnings()
                    .withTotalShareholderEquity("100000")
                    .withCommonStock("30000")
                    .withAdditionalPaidInCapital("20000")
                    .build();
            var balanceSheet = aRawBalanceSheet().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, null, null);

            // Then — 100000 - 30000 - 20000 = 50000
            assertThat(result.retainedEarnings())
                    .isEqualByComparingTo(new BigDecimal("50000"));
        }

        @Test
        @DisplayName("Fallback with null additionalPaidInCapital treats it as zero")
        void fallbackWithNullAPIC() {
            // Given
            var report = RawBalanceSheetFixture.aReport()
                    .withNullRetainedEarnings()
                    .withTotalShareholderEquity("100000")
                    .withCommonStock("30000")
                    .withNullAdditionalPaidInCapital()
                    .build();
            var balanceSheet = aRawBalanceSheet().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, null, null);

            // Then — 100000 - 30000 - 0 = 70000
            assertThat(result.retainedEarnings())
                    .isEqualByComparingTo(new BigDecimal("70000"));
        }

        @Test
        @DisplayName("Returns null when retainedEarnings and commonStock both null")
        void returnsNullWhenFallbackImpossible() {
            // Given
            var report = RawBalanceSheetFixture.aReport()
                    .withNullRetainedEarnings()
                    .withNullCommonStock()
                    .build();
            var balanceSheet = aRawBalanceSheet().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, null, null);

            // Then
            assertThat(result.retainedEarnings()).isNull();
        }
    }

    // ==================== resolveEbit ====================

    @Nested
    @DisplayName("resolveEbit")
    class ResolveEbitTests {

        @Test
        @DisplayName("Uses ebit directly when present")
        void usesDirectValue() {
            // Given
            var report = RawIncomeStatementFixture.aReport()
                    .withEbit("123216000000")
                    .build();
            var incomeStatement = aRawIncomeStatement().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then
            assertThat(result.ebit())
                    .isEqualByComparingTo(new BigDecimal("123216000000"));
        }

        @Test
        @DisplayName("Fallback: netIncome + interestExpense + incomeTaxExpense")
        void fallbackCalculation() {
            // Given
            var report = RawIncomeStatementFixture.aReport()
                    .withNullEbit()
                    .withNetIncome("93736000000")
                    .withInterestExpense("3200000000")
                    .withIncomeTaxExpense("29749000000")
                    .build();
            var incomeStatement = aRawIncomeStatement().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then — 93736000000 + 3200000000 + 29749000000 = 126685000000
            assertThat(result.ebit())
                    .isEqualByComparingTo(new BigDecimal("126685000000"));
        }

        @Test
        @DisplayName("Fallback with null interestExpense and incomeTaxExpense treats them as zero")
        void fallbackWithNullComponents() {
            // Given
            var report = RawIncomeStatementFixture.aReport()
                    .withNullEbit()
                    .withNetIncome("50000")
                    .withNullInterestExpense()
                    .withNullIncomeTaxExpense()
                    .build();
            var incomeStatement = aRawIncomeStatement().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then — 50000 + 0 + 0 = 50000
            assertThat(result.ebit()).isEqualByComparingTo(new BigDecimal("50000"));
        }

        @Test
        @DisplayName("Returns null when both ebit and netIncome are null")
        void returnsNullWhenFallbackImpossible() {
            // Given
            var report = RawIncomeStatementFixture.aReport()
                    .withNullEbit()
                    .withNullNetIncome()
                    .build();
            var incomeStatement = aRawIncomeStatement().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then
            assertThat(result.ebit()).isNull();
        }
    }

    // ==================== calculateTotalDebt ====================

    @Nested
    @DisplayName("calculateTotalDebt")
    class CalculateTotalDebtTests {

        @Test
        @DisplayName("Uses shortLongTermDebtTotal when present")
        void usesPreCalculatedValue() {
            // Given
            var report = RawBalanceSheetFixture.aReport()
                    .withShortLongTermDebtTotal("118815000000")
                    .build();
            var balanceSheet = aRawBalanceSheet().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, null, null);

            // Then
            assertThat(result.totalDebt())
                    .isEqualByComparingTo(new BigDecimal("118815000000"));
        }

        @Test
        @DisplayName("Fallback: shortTermDebt + longTermDebt when shortLongTermDebtTotal is null")
        void fallbackCalculation() {
            // Given
            var report = RawBalanceSheetFixture.aReport()
                    .withNullShortLongTermDebtTotal()
                    .withShortTermDebt("22511000000")
                    .withLongTermDebt("96304000000")
                    .build();
            var balanceSheet = aRawBalanceSheet().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, null, null);

            // Then — 22511000000 + 96304000000 = 118815000000
            assertThat(result.totalDebt())
                    .isEqualByComparingTo(new BigDecimal("118815000000"));
        }

        @Test
        @DisplayName("Fallback with null shortTermDebt and longTermDebt yields zero")
        void fallbackWithNullComponents() {
            // Given
            var report = RawBalanceSheetFixture.aReport()
                    .withNullShortLongTermDebtTotal()
                    .withNullShortTermDebt()
                    .withNullLongTermDebt()
                    .build();
            var balanceSheet = aRawBalanceSheet().withQuarterlyReports(report).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    balanceSheet, null, null);

            // Then — 0 + 0 = 0
            assertThat(result.totalDebt()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Returns null when balance sheet is null")
        void returnsNullWhenBalanceSheetNull() {
            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(null, null, null);

            // Then
            assertThat(result.totalDebt()).isNull();
        }
    }

    // ==================== calculateRevenueTTM ====================

    @Nested
    @DisplayName("calculateRevenueTTM")
    class CalculateRevenueTTMTests {

        @Test
        @DisplayName("Sums totalRevenue from 4 most recent quarterly reports")
        void sumsFourQuarters() {
            // Given
            var q1 = RawIncomeStatementFixture.aReport().withTotalRevenue("100000").build();
            var q2 = RawIncomeStatementFixture.aReport().withTotalRevenue("110000").build();
            var q3 = RawIncomeStatementFixture.aReport().withTotalRevenue("120000").build();
            var q4 = RawIncomeStatementFixture.aReport().withTotalRevenue("130000").build();
            var incomeStatement = aRawIncomeStatement()
                    .withQuarterlyReports(q1, q2, q3, q4).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then — 100000 + 110000 + 120000 + 130000 = 460000
            assertThat(result.revenueTTM())
                    .isEqualByComparingTo(new BigDecimal("460000"));
        }

        @Test
        @DisplayName("Uses only first 4 reports when more than 4 are present")
        void limitsToFourQuarters() {
            // Given — 5 reports, only first 4 should be summed
            var q1 = RawIncomeStatementFixture.aReport().withTotalRevenue("100000").build();
            var q2 = RawIncomeStatementFixture.aReport().withTotalRevenue("100000").build();
            var q3 = RawIncomeStatementFixture.aReport().withTotalRevenue("100000").build();
            var q4 = RawIncomeStatementFixture.aReport().withTotalRevenue("100000").build();
            var q5 = RawIncomeStatementFixture.aReport().withTotalRevenue("999999").build();
            var incomeStatement = aRawIncomeStatement()
                    .withQuarterlyReports(q1, q2, q3, q4, q5).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then — 4 * 100000 = 400000 (q5 excluded)
            assertThat(result.revenueTTM())
                    .isEqualByComparingTo(new BigDecimal("400000"));
        }

        @Test
        @DisplayName("Skips null revenue values in quarterly reports")
        void skipsNullRevenue() {
            // Given — 4 reports, 2 with null revenue
            var q1 = RawIncomeStatementFixture.aReport().withTotalRevenue("100000").build();
            var q2 = RawIncomeStatementFixture.aReport().withNullTotalRevenue().build();
            var q3 = RawIncomeStatementFixture.aReport().withTotalRevenue("100000").build();
            var q4 = RawIncomeStatementFixture.aReport().withNullTotalRevenue().build();
            var incomeStatement = aRawIncomeStatement()
                    .withQuarterlyReports(q1, q2, q3, q4).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then — 100000 + 0(skipped) + 100000 + 0(skipped) = 200000
            assertThat(result.revenueTTM())
                    .isEqualByComparingTo(new BigDecimal("200000"));
        }

        @Test
        @DisplayName("Returns null when income statement is null")
        void nullIncomeStatement() {
            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(null, null, null);

            // Then
            assertThat(result.revenueTTM()).isNull();
        }

        @Test
        @DisplayName("Returns null when quarterly reports list is null")
        void nullQuarterlyReportsList() {
            // Given
            var incomeStatement = aRawIncomeStatement().withNullQuarterlyReports().build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then
            assertThat(result.revenueTTM()).isNull();
        }

        @Test
        @DisplayName("Returns null when quarterly reports list is empty")
        void emptyQuarterlyReportsList() {
            // Given
            var incomeStatement = aRawIncomeStatement()
                    .withQuarterlyReports(List.of()).build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, incomeStatement, null);

            // Then
            assertThat(result.revenueTTM()).isNull();
        }
    }

    // ==================== operatingCashFlow ====================

    @Nested
    @DisplayName("operatingCashFlow mapping")
    class OperatingCashFlowTests {

        @Test
        @DisplayName("Maps operatingCashflow from cash flow report")
        void mapsOperatingCashFlow() {
            // Given
            var cashFlow = aRawCashFlow().build();

            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, null, cashFlow);

            // Then
            assertThat(result.operatingCashFlow())
                    .isEqualByComparingTo(new BigDecimal("36214000000"));
        }

        @Test
        @DisplayName("operatingCashFlow is null when cashFlow is null")
        void nullCashFlow() {
            // When
            FinancialDataSnapshot result = mapper.toFinancialDataSnapshot(
                    null, null, null);

            // Then
            assertThat(result.operatingCashFlow()).isNull();
        }
    }
}
