package com.stock.screener.adapter.web.out.model.chart;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Dane porównawcze dla innych symboli na wykresie.
 */
@Builder
@Jacksonized
public record ChartComparison(
        String symbol,
        Double chartPreviousClose,
        List<Double> high,
        List<Double> low,
        List<Double> open,
        List<Double> close
) {
}

