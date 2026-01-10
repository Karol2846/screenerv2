package com.stock.screener.adapter.web.out.yhfinance.mapper;

import com.stock.screener.adapter.web.out.yhfinance.model.*;
import com.stock.screener.application.port.out.command.QuoteSummaryCommand;
import com.stock.screener.domain.valueobject.Sector;

import java.math.BigDecimal;

public class YhFinanceClientMapper {

    QuoteSummaryCommand toCommand(String ticker, QuoteSummaryResult result) {

        //FIXME: if company is not (nativly) from US
        // - financialData are in different currency -> field financialCurrency
        // -> it need to be racalculated to USD (for now from properties)
        AssetProfile assetProfile = result.assetProfile();
        SummaryDetail summaryDetail = result.summaryDetail();
        EarningsTrend earningsTrend = result.earningsTrend();
        FinancialData financialData = result.financialData();
        RecommendationTrend recommendationTrend = result.recommendationTrend();

        return QuoteSummaryCommand.builder()
                .ticker(ticker)
                .sector(Sector.fromString(assetProfile.sectorKey()))
                .currentPrice(map(financialData.currentPrice()))
                .marketCap(map(summaryDetail.marketCap()))
                .forwardPeRatio(map(summaryDetail.forwardPE()))
                // FIXME: based on yhFinance data I can have or TTM or forward.
                //  I prefer forward, but it needs to ba calculated (with possible currency adjustments)
                .psRatio(map(summaryDetail.priceToSalesTrailing12Months()))



                .operatingCashFlow(map(financialData.operatingCashflow()))      //recalculate to proper value
                .build();
    }

    private static BigDecimal map(RawFmtValue rawFmtValue) {
        return BigDecimal.valueOf(rawFmtValue.raw());
    }

}
