package com.stock.screener.adapter.web.out.model.spark;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Dane historyczne dla pojedynczego symbolu z endpointa /v8/finance/spark.
 * Odpowiedź jest mapą symbol -> SparkData.
 */
@Builder
@Jacksonized
public record SparkData(
        String symbol,
        Double previousClose,
        Double chartPreviousClose,
        List<Long> timestamp,
        List<Double> close,
        Long start,
        Long end,
        Integer dataGranularity
) {
}

