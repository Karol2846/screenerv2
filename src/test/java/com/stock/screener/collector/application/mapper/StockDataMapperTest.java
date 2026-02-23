package com.stock.screener.collector.application.mapper;

import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;
import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StockDataMapper Tests")
class StockDataMapperTest {

    private StockDataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StockDataMapper();
    }

    @Nested
    @DisplayName("toMarketDataSnapshot - Bounded Context Integrity")
    class MarketDataSnapshotBoundedContext {

        @Test
        @DisplayName("revenueTTM is sourced from SEC filings (income statement), NOT from OVERVIEW endpoint")
        void revenueTTM_shouldComeFromIncomeStatement_notFromOverview() {
            // Given: OVERVIEW has revenueTTM = 999 (volatile, should be IGNORED)
            var overview = RawOverview.builder()
                    .revenueTTM(new BigDecimal("999"))
                    .marketCapitalization(new BigDecimal("1000000000"))
                    .build();

            // And: Income statement has 4 quarterly revenues summing to 400
            var incomeStatement = RawIncomeStatement.builder()
                    .quarterlyReports(List.of(
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("100")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("100")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("100")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("100")).build()
                    ))
                    .build();

            // When: Building market data snapshot
            var snapshot = mapper.toMarketDataSnapshot(overview, null, incomeStatement);

            // Then: revenueTTM should be 400 (from SEC filings), NOT 999 (from OVERVIEW)
            assertThat(snapshot.revenueTTM())
                    .isEqualByComparingTo(new BigDecimal("400"));
        }

        @Test
        @DisplayName("revenueTTM is null when income statement is null")
        void revenueTTM_shouldBeNull_whenIncomeStatementIsNull() {
            var overview = RawOverview.builder()
                    .revenueTTM(new BigDecimal("999"))
                    .build();

            var snapshot = mapper.toMarketDataSnapshot(overview, null, null);

            assertThat(snapshot.revenueTTM()).isNull();
        }

        @Test
        @DisplayName("revenueTTM sums only up to 4 most recent quarters")
        void revenueTTM_shouldSumOnly4MostRecentQuarters() {
            var incomeStatement = RawIncomeStatement.builder()
                    .quarterlyReports(List.of(
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("100")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("200")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("300")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("400")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("500")).build()
                    ))
                    .build();

            var snapshot = mapper.toMarketDataSnapshot(null, null, incomeStatement);

            // Should sum first 4: 100 + 200 + 300 + 400 = 1000
            assertThat(snapshot.revenueTTM())
                    .isEqualByComparingTo(new BigDecimal("1000"));
        }

        @Test
        @DisplayName("Market data fields are correctly mapped from respective sources")
        void marketDataFields_shouldBeMappedFromCorrectSources() {
            var overview = RawOverview.builder()
                    .marketCapitalization(new BigDecimal("5000000000"))
                    .forwardPE(new BigDecimal("25.0"))
                    .analystTargetPrice(new BigDecimal("180.00"))
                    .build();

            var yhResponse = YhFinanceResponse.builder()
                    .currentPrice(new BigDecimal("150.00"))
                    .forwardEpsGrowth(new BigDecimal("0.15"))
                    .forwardRevenueGrowth(new BigDecimal("0.10"))
                    .build();

            var incomeStatement = RawIncomeStatement.builder()
                    .quarterlyReports(List.of(
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("250000000")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("250000000")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("250000000")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("250000000")).build()
                    ))
                    .build();

            var snapshot = mapper.toMarketDataSnapshot(overview, yhResponse, incomeStatement);

            // AV Overview fields
            assertThat(snapshot.marketCap()).isEqualByComparingTo(new BigDecimal("5000000000"));
            assertThat(snapshot.forwardPeRatio()).isEqualByComparingTo(new BigDecimal("25.0"));
            assertThat(snapshot.targetPrice()).isEqualByComparingTo(new BigDecimal("180.00"));

            // YH Finance fields
            assertThat(snapshot.currentPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
            assertThat(snapshot.forwardEpsGrowth()).isEqualByComparingTo(new BigDecimal("0.15"));
            assertThat(snapshot.forwardRevenueGrowth()).isEqualByComparingTo(new BigDecimal("0.10"));

            // SEC filing-based revenueTTM
            assertThat(snapshot.revenueTTM()).isEqualByComparingTo(new BigDecimal("1000000000"));
        }

        @Test
        @DisplayName("All sources null produces snapshot with all null fields")
        void allSourcesNull_shouldProduceEmptySnapshot() {
            var snapshot = mapper.toMarketDataSnapshot(null, null, null);

            assertThat(snapshot.marketCap()).isNull();
            assertThat(snapshot.currentPrice()).isNull();
            assertThat(snapshot.revenueTTM()).isNull();
            assertThat(snapshot.forwardPeRatio()).isNull();
            assertThat(snapshot.targetPrice()).isNull();
            assertThat(snapshot.forwardEpsGrowth()).isNull();
            assertThat(snapshot.forwardRevenueGrowth()).isNull();
            assertThat(snapshot.analystRatings()).isNull();
        }
    }
}
