package com.stock.screener.adapter.web.out.model.chart;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Metadane wykresu.
 */
@Builder
@Jacksonized
public record ChartMeta(
        String currency,
        String symbol,
        String exchangeName,
        String instrumentType,
        Long firstTradeDate,
        Long regularMarketTime,
        Integer gmtoffset,
        String timezone,
        String exchangeTimezoneName,
        Double regularMarketPrice,
        Double chartPreviousClose,
        Integer priceHint,
        CurrentTradingPeriod currentTradingPeriod,
        String dataGranularity,
        String range,
        List<String> validRanges
) {

    @Builder
    @Jacksonized
    public record CurrentTradingPeriod(
            TradingPeriod pre,
            TradingPeriod regular,
            TradingPeriod post
    ) {
    }

    @Builder
    @Jacksonized
    public record TradingPeriod(
            String timezone,
            Long start,
            Long end,
            Integer gmtoffset
    ) {
    }
}

