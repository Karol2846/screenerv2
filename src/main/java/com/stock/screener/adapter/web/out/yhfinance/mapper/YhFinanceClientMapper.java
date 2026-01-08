package com.stock.screener.adapter.web.out.yhfinance.mapper;

import com.stock.screener.adapter.web.out.yhfinance.model.*;
import com.stock.screener.application.port.out.command.QuoteSummaryCommand;
import com.stock.screener.domain.valueobject.Sector;

import java.math.BigDecimal;

public class YhFinanceClientMapper {

    QuoteSummaryCommand toCommand(String ticker, QuoteSummaryResult result) {
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
                //FIXME: I dont want to have trailing 12 months...it's not current.
                .psRatio(map(summaryDetail.priceToSalesTrailing12Months()))


                //FIXME: remove this, mapping not finished
                .build();
    }

    private static BigDecimal map(RawFmtValue rawFmtValue) {
        return BigDecimal.valueOf(rawFmtValue.raw());
    }

}
