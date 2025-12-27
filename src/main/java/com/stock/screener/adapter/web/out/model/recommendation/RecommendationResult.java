package com.stock.screener.adapter.web.out.model.recommendation;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wynik rekomendacji dla symbolu.
 */
@Builder
@Jacksonized
public record RecommendationResult(
        String symbol,
        List<RecommendedSymbol> recommendedSymbols
) {

    @Builder
    @Jacksonized
    public record RecommendedSymbol(
            String symbol,
            Double score
    ) {
    }
}

