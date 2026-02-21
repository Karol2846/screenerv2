package com.stock.screener.collector.adapter.out.web.yhfinance;

import com.stock.screener.collector.adapter.out.web.yhfinance.model.*;
import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.domain.valueobject.AnalystRatings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("YhFinanceClientMapper Tests")
class YhFinanceClientMapperTest {

    @Test
    @DisplayName("toCommand maps forwardEpsGrowth correctly")
    void testMapsForwardEpsGrowth() {
        // Given
        QuoteSummaryResult result = buildResult(0.15, 0.20, 10, 5, 8, 2, 1);

        // When
        YhFinanceResponse command = YhFinanceClientMapper.toCommand("AAPL", result);

        // Then
        assertThat(command.forwardEpsGrowth()).isEqualByComparingTo(BigDecimal.valueOf(0.15));
    }

    @Test
    @DisplayName("toCommand maps forwardRevenueGrowth correctly")
    void testMapsForwardRevenueGrowth() {
        // Given
        QuoteSummaryResult result = buildResult(0.15, 0.20, 10, 5, 8, 2, 1);

        // When
        YhFinanceResponse command = YhFinanceClientMapper.toCommand("AAPL", result);

        // Then
        assertThat(command.forwardRevenueGrowth()).isEqualByComparingTo(BigDecimal.valueOf(0.20));
    }

    @Test
    @DisplayName("toCommand maps analystRatings correctly")
    void testMapsAnalystRatings() {
        // Given
        QuoteSummaryResult result = buildResult(0.15, 0.20, 10, 5, 8, 2, 1);

        // When
        YhFinanceResponse command = YhFinanceClientMapper.toCommand("AAPL", result);

        // Then
        AnalystRatings ratings = command.analystRatings();
        assertThat(ratings.strongBuy()).isEqualTo(10);
        assertThat(ratings.buy()).isEqualTo(5);
        assertThat(ratings.hold()).isEqualTo(8);
        assertThat(ratings.sell()).isEqualTo(2);
        assertThat(ratings.strongSell()).isEqualTo(1);
    }

    @Test
    @DisplayName("toCommand sets ticker correctly")
    void testSetsTicker() {
        // Given
        QuoteSummaryResult result = buildResult(0.10, 0.12, 3, 2, 1, 0, 0);

        // When
        YhFinanceResponse command = YhFinanceClientMapper.toCommand("MSFT", result);

        // Then
        assertThat(command.ticker()).isEqualTo("MSFT");
    }

    @Test
    @DisplayName("toCommand uses last trend item for forward estimates")
    void testUsesLastTrendItemForForwardEstimates() {
        // Given - two trend items, mapper should use the last one
        EarningsTrendItem firstItem = buildTrendItem(0.05, 0.06);
        EarningsTrendItem lastItem = buildTrendItem(0.25, 0.30);
        RecommendationTrendItem recommendation = buildRecommendation(1, 1, 1, 1, 1);

        QuoteSummaryResult result = QuoteSummaryResult.builder()
                .earningsTrend(EarningsTrend.builder().trend(List.of(firstItem, lastItem)).build())
                .recommendationTrend(RecommendationTrend.builder().trend(List.of(recommendation)).build())
                .build();

        // When
        YhFinanceResponse command = YhFinanceClientMapper.toCommand("TSLA", result);

        // Then
        assertThat(command.forwardEpsGrowth()).isEqualByComparingTo(BigDecimal.valueOf(0.25));
        assertThat(command.forwardRevenueGrowth()).isEqualByComparingTo(BigDecimal.valueOf(0.30));
    }

    private static QuoteSummaryResult buildResult(double epsGrowth, double revenueGrowth,
                                                   int strongBuy, int buy, int hold, int sell, int strongSell) {
        EarningsTrendItem trendItem = buildTrendItem(epsGrowth, revenueGrowth);
        RecommendationTrendItem recommendation = buildRecommendation(strongBuy, buy, hold, sell, strongSell);

        return QuoteSummaryResult.builder()
                .earningsTrend(EarningsTrend.builder().trend(List.of(trendItem)).build())
                .recommendationTrend(RecommendationTrend.builder().trend(List.of(recommendation)).build())
                .build();
    }

    private static EarningsTrendItem buildTrendItem(double epsGrowth, double revenueGrowth) {
        return EarningsTrendItem.builder()
                .period("+1y")
                .growth(RawFmtValue.builder().raw(epsGrowth).build())
                .revenueEstimate(RevenueEstimate.builder()
                        .growth(RawFmtValue.builder().raw(revenueGrowth).build())
                        .build())
                .build();
    }

    private static RecommendationTrendItem buildRecommendation(int strongBuy, int buy, int hold, int sell, int strongSell) {
        return RecommendationTrendItem.builder()
                .period("0m")
                .strongBuy(strongBuy)
                .buy(buy)
                .hold(hold)
                .sell(sell)
                .strongSell(strongSell)
                .build();
    }
}
