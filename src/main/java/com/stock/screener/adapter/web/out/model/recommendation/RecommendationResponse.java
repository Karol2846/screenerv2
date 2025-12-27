package com.stock.screener.adapter.web.out.model.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v6/finance/recommendationsbysymbol/{symbol}
 */
@Builder
@Jacksonized
public record RecommendationResponse(
        @JsonProperty("finance")
        FinanceResult finance
) {

    @Builder
    @Jacksonized
    public record FinanceResult(
            List<RecommendationResult> result,
            String error
    ) {
    }
}

