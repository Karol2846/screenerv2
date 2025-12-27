package com.stock.screener.adapter.web.out.model.screener;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /ws/screeners/v1/finance/screener/predefined/saved
 */
@Builder
@Jacksonized
public record ScreenerResponse(
        @JsonProperty("finance")
        FinanceResult finance
) {

    @Builder
    @Jacksonized
    public record FinanceResult(
            List<ScreenerResult> result,
            String error
    ) {
    }
}

