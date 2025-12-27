package com.stock.screener.adapter.web.out.model.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v6/finance/quote
 */
@Builder
@Jacksonized
public record QuoteResponse(
        @JsonProperty("quoteResponse")
        QuoteResponseBody quoteResponse
) {

    @Builder
    @Jacksonized
    public record QuoteResponseBody(
            List<Quote> result,
            String error
    ) {
    }
}

