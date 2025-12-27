package com.stock.screener.adapter.web.out.model.quotesummary;

import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Dane cenowe z modułu price.
 */
@Builder
@Jacksonized
public record Price(
        String symbol,
        String shortName,
        String longName,
        String currency,
        String currencySymbol,
        String exchange,
        String exchangeName,
        String quoteType,
        String marketState,
        FormattedValue regularMarketPrice,
        FormattedValue regularMarketChange,
        FormattedValue regularMarketChangePercent,
        FormattedValue regularMarketVolume,
        FormattedValue regularMarketOpen,
        FormattedValue regularMarketDayHigh,
        FormattedValue regularMarketDayLow,
        FormattedValue regularMarketPreviousClose,
        FormattedValue regularMarketTime,
        FormattedValue preMarketPrice,
        FormattedValue preMarketChange,
        FormattedValue preMarketChangePercent,
        FormattedValue preMarketTime,
        FormattedValue postMarketPrice,
        FormattedValue postMarketChange,
        FormattedValue postMarketChangePercent,
        FormattedValue postMarketTime,
        FormattedValue marketCap,
        Integer priceHint,
        Integer maxAge
) {
}

