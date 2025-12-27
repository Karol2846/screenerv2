package com.stock.screener.adapter.web.out.model.marketsummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v6/finance/quote/marketSummary
 */
@Builder
@Jacksonized
public record MarketSummaryResponse(
        @JsonProperty("marketSummaryResponse")
        MarketSummaryBody marketSummaryResponse
) {

    @Builder
    @Jacksonized
    public record MarketSummaryBody(
            List<MarketSummaryQuote> result,
            String error
    ) {
    }
}

