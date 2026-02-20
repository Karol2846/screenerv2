package com.stock.screener.adapter.web.out.yhfinance;

import com.stock.screener.adapter.web.out.yhfinance.model.EarningsTrendItem;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResult;
import com.stock.screener.adapter.web.out.yhfinance.model.RawFmtValue;
import com.stock.screener.adapter.web.out.yhfinance.model.RecommendationTrendItem;
import com.stock.screener.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.domain.valueobject.AnalystRatings;
import java.math.BigDecimal;

class YhFinanceClientMapper {

    static YhFinanceResponse toCommand(String ticker, QuoteSummaryResult result) {
        EarningsTrendItem forwardEstimates = result.earningsTrend().trend().getLast();
        RecommendationTrendItem recommendations = result.recommendationTrend().trend().getFirst();

        return YhFinanceResponse.builder()
                .ticker(ticker)
                .forwardEpsGrowth(map(forwardEstimates.growth()))
                .forwardRevenueGrowth(map(forwardEstimates.revenueEstimate().growth()))
                .analystRatings(AnalystRatings.builder()
                        .strongBuy(recommendations.strongBuy())
                        .buy(recommendations.buy())
                        .hold(recommendations.hold())
                        .sell(recommendations.sell())
                        .strongSell(recommendations.strongSell())
                        .build())
                .build();
    }

    private static BigDecimal map(RawFmtValue rawFmtValue) {
        return BigDecimal.valueOf(rawFmtValue.raw());
    }
}
