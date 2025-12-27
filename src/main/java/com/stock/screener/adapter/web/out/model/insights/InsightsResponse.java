package com.stock.screener.adapter.web.out.model.insights;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Odpowiedź z endpointa /ws/insights/v1/finance/insights
 */
@Builder
@Jacksonized
public record InsightsResponse(
        @JsonProperty("finance")
        FinanceResult finance
) {

    @Builder
    @Jacksonized
    public record FinanceResult(
            InsightsResult result,
            String error
    ) {
    }
}

