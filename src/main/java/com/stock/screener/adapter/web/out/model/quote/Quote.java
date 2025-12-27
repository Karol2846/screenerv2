package com.stock.screener.adapter.web.out.model.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Quote (wycena) - dane giełdowe dla pojedynczego instrumentu.
 * Używane w /v6/finance/quote oraz jako pole zagnieżdżone w innych odpowiedziach.
 */
@Builder
@Jacksonized
public record Quote(
        String symbol,
        String shortName,
        String longName,
        String displayName,
        String currency,
        String financialCurrency,
        String exchange,
        String fullExchangeName,
        String quoteType,
        String quoteSourceName,
        String market,
        String marketState,
        String region,
        String language,

        // Bid/Ask
        Double ask,
        Integer askSize,
        Double bid,
        Integer bidSize,

        // Regular market data
        Double regularMarketPrice,
        Double regularMarketChange,
        Double regularMarketChangePercent,
        Double regularMarketOpen,
        Double regularMarketDayHigh,
        Double regularMarketDayLow,
        String regularMarketDayRange,
        Double regularMarketPreviousClose,
        Long regularMarketVolume,
        Long regularMarketTime,

        // Post market
        Double postMarketPrice,
        Double postMarketChange,
        Double postMarketChangePercent,
        Long postMarketTime,

        // 52 week stats
        @JsonProperty("fiftyTwoWeekHigh")
        Double fiftyTwoWeekHigh,

        @JsonProperty("fiftyTwoWeekLow")
        Double fiftyTwoWeekLow,

        @JsonProperty("fiftyTwoWeekHighChange")
        Double fiftyTwoWeekHighChange,

        @JsonProperty("fiftyTwoWeekLowChange")
        Double fiftyTwoWeekLowChange,

        @JsonProperty("fiftyTwoWeekHighChangePercent")
        Double fiftyTwoWeekHighChangePercent,

        @JsonProperty("fiftyTwoWeekLowChangePercent")
        Double fiftyTwoWeekLowChangePercent,

        @JsonProperty("fiftyTwoWeekRange")
        String fiftyTwoWeekRange,

        // Moving averages
        @JsonProperty("fiftyDayAverage")
        Double fiftyDayAverage,

        @JsonProperty("fiftyDayAverageChange")
        Double fiftyDayAverageChange,

        @JsonProperty("fiftyDayAverageChangePercent")
        Double fiftyDayAverageChangePercent,

        @JsonProperty("twoHundredDayAverage")
        Double twoHundredDayAverage,

        @JsonProperty("twoHundredDayAverageChange")
        Double twoHundredDayAverageChange,

        @JsonProperty("twoHundredDayAverageChangePercent")
        Double twoHundredDayAverageChangePercent,

        // Volume averages
        @JsonProperty("averageDailyVolume10Day")
        Long averageDailyVolume10Day,

        @JsonProperty("averageDailyVolume3Month")
        Long averageDailyVolume3Month,

        // Market cap & shares
        Long marketCap,
        Long sharesOutstanding,

        // Valuation
        Double bookValue,
        Integer priceHint,
        Double priceToBook,
        Double forwardPE,
        Double trailingPE,
        Double priceEpsCurrentYear,

        // EPS
        Double epsCurrentYear,
        Double epsForward,
        Double epsTrailingTwelveMonths,

        // Dividends
        Long dividendDate,
        Double trailingAnnualDividendRate,
        Double trailingAnnualDividendYield,

        // Earnings
        Long earningsTimestamp,
        Long earningsTimestampStart,
        Long earningsTimestampEnd,

        // Misc
        Boolean tradeable,
        Boolean triggerable,
        Boolean esgPopulated,
        Integer sourceInterval,
        Integer exchangeDataDelayedBy,
        Long gmtOffSetMilliseconds,
        String exchangeTimezoneName,
        String exchangeTimezoneShortName,
        Long firstTradeDateMilliseconds,
        String messageBoardId,
        String averageAnalystRating
) {
}

