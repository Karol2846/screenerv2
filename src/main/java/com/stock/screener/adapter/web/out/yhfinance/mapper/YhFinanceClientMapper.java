package com.stock.screener.adapter.web.out.yhfinance.mapper;

import com.stock.screener.adapter.web.out.yhfinance.model.*;
import com.stock.screener.application.port.out.command.QuoteSummaryCommand;
import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.Sector;

import java.math.BigDecimal;

public class YhFinanceClientMapper {

    //TODO: introduce separate class for currency recalculation (for now properties)
    //  currency can ba taken from:
    // - financialData.financialCurrency
    // - earningTrend.trend[*].*.*currency - np. trend[-1]revenueEstimates.revenueCurrency

    QuoteSummaryCommand toCommand(String ticker, QuoteSummaryResult result) {
        SummaryDetail summaryDetail = result.summaryDetail();
        FinancialData financialData = result.financialData();
        EarningsTrendItem forwardEstimates = result.earningsTrend().trend().getLast();
        RecommendationTrendItem recomendations = result.recommendationTrend().trend().getFirst();

        return QuoteSummaryCommand.builder()
                .ticker(ticker)
                .sector(Sector.fromString(result.assetProfile().sectorKey()))
                .currentPrice(map(financialData.currentPrice()))                //(no need for currency calculation)
                .marketCap(map(summaryDetail.marketCap()))
                .forwardPeRatio(map(summaryDetail.forwardPE()))
                .forwardRevenue(map(forwardEstimates.revenueEstimate().avg()))  //currency!
                .forwardRevenueGrowth(map(forwardEstimates.revenueEstimate().growth()))
                .forwardEpsGrowth(map(forwardEstimates.growth()))
                .targetPrice(map(financialData.targetMeanPrice()))              //(no need for currency calculation)
                .analystRatings(AnalystRatings.builder()
                        .strongBuy(recomendations.strongBuy())
                        .buy(recomendations.buy())
                        .hold(recomendations.hold())
                        .sell(recomendations.sell())
                        .strongSell(recomendations.strongSell())
                        .build())
                .operatingCashFlow(map(financialData.operatingCashflow()))      //currency!
                .build();
    }

    private static BigDecimal map(RawFmtValue rawFmtValue) {
        return BigDecimal.valueOf(rawFmtValue.raw());
    }

}
