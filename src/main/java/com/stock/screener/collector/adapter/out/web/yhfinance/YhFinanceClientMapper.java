package com.stock.screener.collector.adapter.out.web.yhfinance;

import com.stock.screener.collector.adapter.out.web.yhfinance.model.EarningsTrendItem;
import com.stock.screener.collector.adapter.out.web.yhfinance.model.QuoteSummaryResult;
import com.stock.screener.collector.adapter.out.web.yhfinance.model.RawFmtValue;
import com.stock.screener.collector.adapter.out.web.yhfinance.model.RecommendationTrendItem;
import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.domain.valueobject.AnalystRatings;
import java.math.BigDecimal;

class YhFinanceClientMapper {

    static YhFinanceResponse toCommand(String ticker, QuoteSummaryResult result) {
        var builder = YhFinanceResponse.builder().ticker(ticker);

        if (result.price() != null) {
            builder.currentPrice(result.price().regularMarketPrice());
        }

        if (result.earningsTrend() != null && result.earningsTrend().trend() != null
                && !result.earningsTrend().trend().isEmpty()) {
            EarningsTrendItem forwardEstimates = result.earningsTrend().trend().getLast();
            builder.forwardEpsGrowth(map(forwardEstimates.growth()));
            if (forwardEstimates.revenueEstimate() != null) {
                builder.forwardRevenueGrowth(map(forwardEstimates.revenueEstimate().growth()));
            }
        }

        if (result.recommendationTrend() != null && result.recommendationTrend().trend() != null
                && !result.recommendationTrend().trend().isEmpty()) {
            RecommendationTrendItem recommendations = result.recommendationTrend().trend().getFirst();
            builder.analystRatings(AnalystRatings.builder()
                    .strongBuy(recommendations.strongBuy())
                    .buy(recommendations.buy())
                    .hold(recommendations.hold())
                    .sell(recommendations.sell())
                    .strongSell(recommendations.strongSell())
                    .build());
        }

        return builder.build();
    }

    private static BigDecimal map(RawFmtValue rawFmtValue) {
        return rawFmtValue != null ? BigDecimal.valueOf(rawFmtValue.raw()) : null;
    }
}
