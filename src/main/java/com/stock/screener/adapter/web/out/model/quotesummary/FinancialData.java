package com.stock.screener.adapter.web.out.model.quotesummary;

import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Dane finansowe firmy.
 */
@Builder
@Jacksonized
public record FinancialData(
        FormattedValue currentPrice,
        FormattedValue targetHighPrice,
        FormattedValue targetLowPrice,
        FormattedValue targetMeanPrice,
        FormattedValue targetMedianPrice,
        String recommendationKey,
        FormattedValue recommendationMean,
        Integer numberOfAnalystOpinions,
        FormattedValue totalCash,
        FormattedValue totalCashPerShare,
        FormattedValue ebitda,
        FormattedValue totalDebt,
        FormattedValue quickRatio,
        FormattedValue currentRatio,
        FormattedValue totalRevenue,
        FormattedValue debtToEquity,
        FormattedValue revenuePerShare,
        FormattedValue returnOnAssets,
        FormattedValue returnOnEquity,
        FormattedValue grossProfits,
        FormattedValue freeCashflow,
        FormattedValue operatingCashflow,
        FormattedValue earningsGrowth,
        FormattedValue revenueGrowth,
        FormattedValue grossMargins,
        FormattedValue ebitdaMargins,
        FormattedValue operatingMargins,
        FormattedValue profitMargins,
        String financialCurrency,
        Integer maxAge
) {
}

