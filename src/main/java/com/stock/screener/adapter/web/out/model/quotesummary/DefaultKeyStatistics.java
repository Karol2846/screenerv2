package com.stock.screener.adapter.web.out.model.quotesummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Domyślne statystyki kluczowe dla akcji.
 */
@Builder
@Jacksonized
public record DefaultKeyStatistics(
        FormattedValue priceHint,
        FormattedValue enterpriseValue,
        FormattedValue forwardPE,
        FormattedValue profitMargins,
        FormattedValue floatShares,
        FormattedValue sharesOutstanding,
        FormattedValue sharesShort,
        FormattedValue sharesShortPriorMonth,
        FormattedValue sharesPercentSharesOut,
        FormattedValue shortRatio,
        FormattedValue shortPercentOfFloat,
        FormattedValue beta,
        FormattedValue bookValue,
        FormattedValue priceToBook,
        FormattedValue lastFiscalYearEnd,
        FormattedValue nextFiscalYearEnd,
        FormattedValue mostRecentQuarter,
        FormattedValue earningsQuarterlyGrowth,
        FormattedValue netIncomeToCommon,
        FormattedValue trailingEps,
        FormattedValue forwardEps,
        FormattedValue pegRatio,
        String lastSplitFactor,
        FormattedValue lastSplitDate,
        FormattedValue enterpriseToRevenue,
        FormattedValue enterpriseToEbitda,

        @JsonProperty("52WeekChange")
        FormattedValue fiftyTwoWeekChange,

        @JsonProperty("SandP52WeekChange")
        FormattedValue sandP52WeekChange,

        FormattedValue dateShortInterest,
        FormattedValue heldPercentInsiders,
        FormattedValue heldPercentInstitutions,
        Integer maxAge
) {
}

