package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record SummaryDetail(
        Integer maxAge,
        RawFmtValue priceHint,
        RawFmtValue previousClose,
        RawFmtValue open,
        RawFmtValue dayLow,
        RawFmtValue dayHigh,
        RawFmtValue regularMarketPreviousClose,
        RawFmtValue regularMarketOpen,
        RawFmtValue regularMarketDayLow,
        RawFmtValue regularMarketDayHigh,
        RawFmtValue dividendRate,
        RawFmtValue dividendYield,
        RawFmtValue exDividendDate,
        RawFmtValue payoutRatio,
        RawFmtValue fiveYearAvgDividendYield,
        RawFmtValue beta,
        RawFmtValue trailingPE,
        RawFmtValue forwardPE,
        RawFmtValue volume,
        RawFmtValue regularMarketVolume,
        RawFmtValue averageVolume,
        RawFmtValue averageVolume10days,
        RawFmtValue averageDailyVolume10Day,
        RawFmtValue bid,
        RawFmtValue ask,
        RawFmtValue bidSize,
        RawFmtValue askSize,
        RawFmtValue marketCap,
        RawFmtValue fiftyTwoWeekLow,
        RawFmtValue fiftyTwoWeekHigh,
        RawFmtValue allTimeHigh,
        RawFmtValue allTimeLow,
        RawFmtValue priceToSalesTrailing12Months,
        RawFmtValue fiftyDayAverage,
        RawFmtValue twoHundredDayAverage,
        RawFmtValue trailingAnnualDividendRate,
        RawFmtValue trailingAnnualDividendYield,
        String currency,
        Boolean tradeable
) {
}

