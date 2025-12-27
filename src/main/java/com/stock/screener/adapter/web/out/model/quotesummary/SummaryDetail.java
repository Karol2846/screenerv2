package com.stock.screener.adapter.web.out.model.quotesummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Szczegóły podsumowania wyceny.
 */
@Builder
@Jacksonized
public record SummaryDetail(
        FormattedValue priceHint,
        FormattedValue previousClose,
        FormattedValue open,
        FormattedValue dayLow,
        FormattedValue dayHigh,
        FormattedValue regularMarketPreviousClose,
        FormattedValue regularMarketOpen,
        FormattedValue regularMarketDayLow,
        FormattedValue regularMarketDayHigh,
        FormattedValue dividendRate,
        FormattedValue dividendYield,
        FormattedValue exDividendDate,
        FormattedValue payoutRatio,

        @JsonProperty("fiveYearAvgDividendYield")
        FormattedValue fiveYearAvgDividendYield,

        FormattedValue beta,
        FormattedValue trailingPE,
        FormattedValue forwardPE,
        FormattedValue volume,
        FormattedValue regularMarketVolume,
        FormattedValue averageVolume,

        @JsonProperty("averageVolume10days")
        FormattedValue averageVolume10Days,

        FormattedValue averageDailyVolume10Day,
        FormattedValue bid,
        FormattedValue ask,
        FormattedValue bidSize,
        FormattedValue askSize,
        FormattedValue marketCap,

        @JsonProperty("fiftyTwoWeekLow")
        FormattedValue fiftyTwoWeekLow,

        @JsonProperty("fiftyTwoWeekHigh")
        FormattedValue fiftyTwoWeekHigh,

        FormattedValue priceToSalesTrailing12Months,

        @JsonProperty("fiftyDayAverage")
        FormattedValue fiftyDayAverage,

        @JsonProperty("twoHundredDayAverage")
        FormattedValue twoHundredDayAverage,

        FormattedValue trailingAnnualDividendRate,
        FormattedValue trailingAnnualDividendYield,
        String currency,
        Integer maxAge
) {
}

