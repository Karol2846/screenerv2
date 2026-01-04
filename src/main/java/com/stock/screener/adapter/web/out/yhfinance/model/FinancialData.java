package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record FinancialData(
        Integer maxAge,
        RawFmtValue currentPrice,
        RawFmtValue targetHighPrice,
        RawFmtValue targetLowPrice,
        RawFmtValue targetMeanPrice,
        RawFmtValue targetMedianPrice,
        RawFmtValue recommendationMean,
        String recommendationKey,
        RawFmtValue numberOfAnalystOpinions,
        RawFmtValue totalCash,
        RawFmtValue totalCashPerShare,
        RawFmtValue ebitda,
        RawFmtValue totalDebt,
        RawFmtValue quickRatio,
        RawFmtValue currentRatio,
        RawFmtValue totalRevenue,
        RawFmtValue debtToEquity,
        RawFmtValue revenuePerShare,
        RawFmtValue returnOnAssets,
        RawFmtValue returnOnEquity,
        RawFmtValue grossProfits,
        RawFmtValue freeCashflow,
        RawFmtValue operatingCashflow,
        RawFmtValue earningsGrowth,
        RawFmtValue revenueGrowth,
        RawFmtValue grossMargins,
        RawFmtValue ebitdaMargins,
        RawFmtValue operatingMargins,
        RawFmtValue profitMargins,
        String financialCurrency
) {
}

