package com.stock.screener.adapter.web.out.model.chart;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wskaźniki wykresu (quote, adjclose).
 */
@Builder
@Jacksonized
public record ChartIndicators(
        List<ChartQuote> quote,
        List<ChartAdjClose> adjclose
) {

    @Builder
    @Jacksonized
    public record ChartQuote(
            List<Double> open,
            List<Double> high,
            List<Double> low,
            List<Double> close,
            List<Long> volume
    ) {
    }

    @Builder
    @Jacksonized
    public record ChartAdjClose(
            List<Double> adjclose
    ) {
    }
}

