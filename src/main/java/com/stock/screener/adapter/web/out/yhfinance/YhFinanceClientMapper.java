package com.stock.screener.adapter.web.out.yhfinance;

import com.stock.screener.adapter.web.out.yhfinance.model.EarningsTrendItem;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResult;
import com.stock.screener.adapter.web.out.yhfinance.model.RawFmtValue;
import com.stock.screener.adapter.web.out.yhfinance.model.RecommendationTrendItem;
import com.stock.screener.application.port.out.yhfinance.command.QuoteSummaryCommand;
import com.stock.screener.domain.valueobject.AnalystRatings;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
class YhFinanceClientMapper {

    QuoteSummaryCommand toCommand(String ticker, QuoteSummaryResult result) {
        EarningsTrendItem forwardEstimates = result.earningsTrend().trend().getLast();
        RecommendationTrendItem recommendations = result.recommendationTrend().trend().getFirst();

        return QuoteSummaryCommand.builder()
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
