package com.stock.screener.adapter.web.out.model.chart;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v8/finance/chart/{ticker}
 */
@Builder
@Jacksonized
public record ChartResponse(
        ChartResult chart
) {

    @Builder
    @Jacksonized
    public record ChartResult(
            List<ChartData> result,
            String error
    ) {
    }
}

