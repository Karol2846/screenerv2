package com.stock.screener.adapter.web.out.model.trending;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v1/finance/trending/{region}
 */
@Builder
@Jacksonized
public record TrendingResponse(
        @JsonProperty("finance")
        FinanceResult finance
) {

    @Builder
    @Jacksonized
    public record FinanceResult(
            List<TrendingResult> result,
            String error
    ) {
    }

    @Builder
    @Jacksonized
    public record TrendingResult(
            Integer count,
            Long jobTimestamp,
            Long startInterval,
            List<TrendingQuote> quotes
    ) {
    }

    @Builder
    @Jacksonized
    public record TrendingQuote(
            String symbol
    ) {
    }
}

