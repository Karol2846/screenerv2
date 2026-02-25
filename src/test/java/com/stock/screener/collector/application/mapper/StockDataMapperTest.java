package com.stock.screener.collector.application.mapper;

import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;
import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.domain.valueobject.snapshot.MarketDataSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StockDataMapper Tests")
class StockDataMapperTest {

    private final StockDataMapper mapper = new StockDataMapper();

    @Nested
    @DisplayName("Bounded Context: revenueTTM sourcing in MarketDataSnapshot")
    class RevenueTTMBoundedContext {

        @Test
        @DisplayName("revenueTTM should be calculated from income statement quarterly reports, not from overview")
        void revenueTTMShouldComeFromIncomeStatement() {
            var overview = RawOverview.builder()
                    .revenueTTM(new BigDecimal("999999999"))
                    .marketCapitalization(new BigDecimal("5000000000"))
                    .build();

            var incomeStatement = RawIncomeStatement.builder()
                    .quarterlyReports(List.of(
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("100")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("200")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("300")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("400")).build()))
                    .build();

            MarketDataSnapshot snapshot = mapper.toMarketDataSnapshot(overview, null, incomeStatement);

            assertThat(snapshot.revenueTTM())
                    .isEqualByComparingTo(new BigDecimal("1000"))
                    .isNotEqualByComparingTo(new BigDecimal("999999999"));
        }

        @Test
        @DisplayName("revenueTTM should be null when income statement is null")
        void revenueTTMShouldBeNullWhenIncomeStatementIsNull() {
            var overview = RawOverview.builder()
                    .revenueTTM(new BigDecimal("999999999"))
                    .build();

            MarketDataSnapshot snapshot = mapper.toMarketDataSnapshot(overview, null, null);

            assertThat(snapshot.revenueTTM()).isNull();
        }

        @Test
        @DisplayName("revenueTTM should sum only 4 most recent quarters")
        void revenueTTMShouldSumFourMostRecentQuarters() {
            var incomeStatement = RawIncomeStatement.builder()
                    .quarterlyReports(List.of(
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("100")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("200")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("300")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("400")).build(),
                            RawIncomeStatement.Report.builder().totalRevenue(new BigDecimal("500")).build()))
                    .build();

            MarketDataSnapshot snapshot = mapper.toMarketDataSnapshot(null, null, incomeStatement);

            assertThat(snapshot.revenueTTM()).isEqualByComparingTo(new BigDecimal("1000"));
        }

        @Test
        @DisplayName("overview fields other than revenueTTM should still be mapped")
        void overviewFieldsShouldStillBeMapped() {
            var overview = RawOverview.builder()
                    .marketCapitalization(new BigDecimal("5000000000"))
                    .forwardPE(new BigDecimal("25.0"))
                    .analystTargetPrice(new BigDecimal("180.00"))
                    .revenueTTM(new BigDecimal("999999999"))
                    .build();

            MarketDataSnapshot snapshot = mapper.toMarketDataSnapshot(overview, null, null);

            assertThat(snapshot.marketCap()).isEqualByComparingTo(new BigDecimal("5000000000"));
            assertThat(snapshot.forwardPeRatio()).isEqualByComparingTo(new BigDecimal("25.0"));
            assertThat(snapshot.targetPrice()).isEqualByComparingTo(new BigDecimal("180.00"));
        }

        @Test
        @DisplayName("Yahoo Finance fields should still be mapped correctly")
        void yahooFinanceFieldsShouldBeMapped() {
            var yhResponse = YhFinanceResponse.builder()
                    .currentPrice(new BigDecimal("150.00"))
                    .forwardEpsGrowth(new BigDecimal("15.0"))
                    .forwardRevenueGrowth(new BigDecimal("12.5"))
                    .build();

            MarketDataSnapshot snapshot = mapper.toMarketDataSnapshot(null, yhResponse, null);

            assertThat(snapshot.currentPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
            assertThat(snapshot.forwardEpsGrowth()).isEqualByComparingTo(new BigDecimal("15.0"));
            assertThat(snapshot.forwardRevenueGrowth()).isEqualByComparingTo(new BigDecimal("12.5"));
        }
    }
}
