package com.stock.screener.adapter.web.out.model.chart;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Dane wykresu dla pojedynczego symbolu.
 */
@Builder
@Jacksonized
public record ChartData(
        ChartMeta meta,
        List<Long> timestamp,
        List<ChartComparison> comparisons,
        ChartIndicators indicators
) {
}

