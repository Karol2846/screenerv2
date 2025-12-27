package com.stock.screener.adapter.web.out.model.marketsummary;

import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Wycena z podsumowania rynku (indeksy, waluty, surowce).
 */
@Builder
@Jacksonized
public record MarketSummaryQuote(
        String symbol,
        String shortName,
        String longName,
        String exchange,
        String fullExchangeName,
        String quoteType,
        String quoteSourceName,
        String market,
        String marketState,
        String region,
        String language,
        String currency,
        FormattedValue regularMarketPrice,
        FormattedValue regularMarketChange,
        FormattedValue regularMarketChangePercent,
        FormattedValue regularMarketPreviousClose,
        FormattedValue regularMarketTime,
        Boolean tradeable,
        Boolean triggerable,
        Boolean contractSymbol,
        Boolean headSymbol,
        String headSymbolAsString,
        Integer priceHint,
        Integer sourceInterval,
        Integer exchangeDataDelayedBy,
        Long gmtOffSetMilliseconds,
        String exchangeTimezoneName,
        String exchangeTimezoneShortName,
        Long firstTradeDateMilliseconds
) {
}

