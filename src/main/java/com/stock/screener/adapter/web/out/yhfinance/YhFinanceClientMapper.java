package com.stock.screener.adapter.web.out.yhfinance;

import com.stock.screener.adapter.web.out.yhfinance.model.*;
import com.stock.screener.application.port.out.yhfinance.command.QuoteSummaryCommand;
import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.Sector;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@ApplicationScoped
@RequiredArgsConstructor
public class YhFinanceClientMapper {

    private final ExchangeRateProperties exchangeRate;

    QuoteSummaryCommand toCommand(String ticker, QuoteSummaryResult result) {
        SummaryDetail summaryDetail = result.summaryDetail();
        FinancialData financialData = result.financialData();
        EarningsTrendItem forwardEstimates = result.earningsTrend().trend().getLast();
        RecommendationTrendItem recommendations = result.recommendationTrend().trend().getFirst();

        return QuoteSummaryCommand.builder()
                .ticker(ticker)
                .sector(Sector.fromString(result.assetProfile().sectorKey()))
                .currentPrice(map(financialData.currentPrice()))                //(no need for currency calculation) - on BABA
                .marketCap(map(summaryDetail.marketCap()))
                .forwardPeRatio(map(summaryDetail.forwardPE()))
                .forwardRevenue(
                        calculateForwardRevenue(forwardEstimates))
                .forwardRevenueGrowth(map(forwardEstimates.revenueEstimate().growth()))
                .forwardEpsGrowth(map(forwardEstimates.growth()))
                .targetPrice(map(financialData.targetMeanPrice()))              //(no need for currency calculation) - on BABA
                .analystRatings(AnalystRatings.builder()
                        .strongBuy(recommendations.strongBuy())
                        .buy(recommendations.buy())
                        .hold(recommendations.hold())
                        .sell(recommendations.sell())
                        .strongSell(recommendations.strongSell())
                        .build())
                .operatingCashFlow(calculateOperatingCashFLow(financialData))
                .build();
    }

    private BigDecimal calculateForwardRevenue(EarningsTrendItem forwardEstimates) {
        return exchangeRate.calculateInUsd(
                forwardEstimates.revenueEstimate().revenueCurrency(),
                map(forwardEstimates.revenueEstimate().avg()));
    }

    private BigDecimal calculateOperatingCashFLow(FinancialData financialData) {
        return exchangeRate.calculateInUsd(
                financialData.financialCurrency(),
                map(financialData.operatingCashflow()));
    }

    private static BigDecimal map(RawFmtValue rawFmtValue) {
        return BigDecimal.valueOf(rawFmtValue.raw());
    }

}
