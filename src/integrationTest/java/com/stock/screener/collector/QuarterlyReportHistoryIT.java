package com.stock.screener.collector;

import com.stock.screener.collector.application.port.in.CollectQuarterlyDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.collector.application.port.out.alphavantage.RawCashFlow;
import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.collector.domain.entity.QuarterlyReport;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(IntegrationTestProfile.class)
@DisplayName("Quarterly Report Collection — History Accumulation")
class QuarterlyReportHistoryIT {

    private static final String TICKER = "HISTQ";
    private static final LocalDate FISCAL_Q4_2025 = LocalDate.of(2025, 12, 31);
    private static final LocalDate FISCAL_Q1_2026 = LocalDate.of(2026, 3, 31);

    @Inject
    CollectQuarterlyDataUseCase useCase;

    @InjectMock
    AlphaVantageClient alphaVantageClient;

    @Nested
    @DisplayName("Multiple fiscal quarters")
    class MultipleFiscalQuarters {

        @Test
        @TestTransaction
        @DisplayName("Two collections with different fiscalDateEnding create two separate rows")
        void shouldCreateSeparateRowPerFiscalQuarter() {
            // Given: AV returns Q4 2025 data on first call
            stubAlphaVantageFor("2025-12-31");

            // When: first quarterly collection runs
            useCase.collectQuarterlyData(TICKER);

            // Given: AV now returns Q1 2026 data on the next call
            stubAlphaVantageFor("2026-03-31");

            // When: second quarterly collection runs
            useCase.collectQuarterlyData(TICKER);

            // Then: both fiscal quarters persisted as separate rows
            assertThat(QuarterlyReport.count("ticker", TICKER)).isEqualTo(2);

            assertThat(findByTickerAndFiscalDate(TICKER, FISCAL_Q4_2025))
                    .as("Q4 2025 row should exist")
                    .isNotNull();

            assertThat(findByTickerAndFiscalDate(TICKER, FISCAL_Q1_2026))
                    .as("Q1 2026 row should exist")
                    .isNotNull();
        }
    }

    @Nested
    @DisplayName("Same fiscal quarter — idempotency")
    class SameFiscalQuarter {

        @Test
        @TestTransaction
        @DisplayName("Two collections with the same fiscalDateEnding upsert the existing row")
        void shouldUpsertWhenSameFiscalQuarter() {
            // Given: AV returns the same fiscal date on both calls
            stubAlphaVantageFor("2025-12-31");

            // When: collection runs twice
            useCase.collectQuarterlyData(TICKER);
            useCase.collectQuarterlyData(TICKER);

            // Then: only one row exists (second call updated, did not duplicate)
            assertThat(QuarterlyReport.count("ticker", TICKER)).isEqualTo(1);

            assertThat(findByTickerAndFiscalDate(TICKER, FISCAL_Q4_2025))
                    .as("Single Q4 2025 row should exist")
                    .isNotNull();
        }
    }

    // --- Helpers ---

    private QuarterlyReport findByTickerAndFiscalDate(String ticker, LocalDate fiscalDate) {
        return QuarterlyReport
                .find("ticker = ?1 and fiscalDateEnding = ?2", ticker, fiscalDate)
                .firstResult();
    }

    private void stubAlphaVantageFor(String fiscalDate) {
        when(alphaVantageClient.fetchBalanceSheet(eq(TICKER))).thenReturn(buildBalanceSheet(fiscalDate));
        when(alphaVantageClient.fetchIncomeStatement(eq(TICKER))).thenReturn(buildIncomeStatement(fiscalDate));
        when(alphaVantageClient.fetchCashFlow(eq(TICKER))).thenReturn(buildCashFlow(fiscalDate));
    }

    private RawBalanceSheet buildBalanceSheet(String fiscalDate) {
        var report = RawBalanceSheet.Report.builder()
                .fiscalDateEnding(fiscalDate)
                .reportedCurrency("USD")
                .totalAssets(new BigDecimal("100000000"))
                .totalCurrentAssets(new BigDecimal("50000000"))
                .totalCurrentLiabilities(new BigDecimal("20000000"))
                .totalLiabilities(new BigDecimal("40000000"))
                .totalShareholderEquity(new BigDecimal("60000000"))
                .retainedEarnings(new BigDecimal("30000000"))
                .shortLongTermDebtTotal(new BigDecimal("15000000"))
                .build();
        return RawBalanceSheet.builder()
                .symbol(TICKER)
                .annualReports(List.of())
                .quarterlyReports(List.of(report))
                .build();
    }

    private RawIncomeStatement buildIncomeStatement(String fiscalDate) {
        var report = RawIncomeStatement.Report.builder()
                .fiscalDateEnding(fiscalDate)
                .reportedCurrency("USD")
                .totalRevenue(new BigDecimal("80000000"))
                .netIncome(new BigDecimal("10000000"))
                .ebit(new BigDecimal("12000000"))
                .interestExpense(new BigDecimal("1000000"))
                .incomeTaxExpense(new BigDecimal("2000000"))
                .build();
        return RawIncomeStatement.builder()
                .symbol(TICKER)
                .annualReports(List.of())
                .quarterlyReports(List.of(report, report, report, report))
                .build();
    }

    private RawCashFlow buildCashFlow(String fiscalDate) {
        var report = RawCashFlow.Report.builder()
                .fiscalDateEnding(fiscalDate)
                .reportedCurrency("USD")
                .operatingCashflow(new BigDecimal("15000000"))
                .build();
        return RawCashFlow.builder()
                .symbol(TICKER)
                .annualReports(List.of())
                .quarterlyReports(List.of(report))
                .build();
    }
}
