package com.stock.screener.adapter.web.out.model.quotesummary;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Typ instrumentu (akcja, ETF, fundusz itp.).
 */
@Builder
@Jacksonized
public record QuoteType(
        String symbol,
        String quoteType,
        String shortName,
        String longName,
        String exchange,
        String market,
        String messageBoardId,
        String exchangeTimezoneName,
        String exchangeTimezoneShortName,
        Long gmtOffSetMilliseconds,
        Long firstTradeDateEpochUtc,
        String timeZoneFullName,
        String timeZoneShortName,
        String uuid,
        Integer maxAge
) {
}

