package com.stock.screener.adapter.web.out.model.autocomplete;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v6/finance/autocomplete
 */
@Builder
@Jacksonized
public record AutocompleteResponse(
        @JsonProperty("ResultSet")
        ResultSet resultSet
) {

    @Builder
    @Jacksonized
    public record ResultSet(
            @JsonProperty("Query")
            String query,

            @JsonProperty("Result")
            List<AutocompleteResult> result
    ) {
    }

    @Builder
    @Jacksonized
    public record AutocompleteResult(
            String symbol,
            String name,
            String exch,
            String exchDisp,
            String type,
            String typeDisp
    ) {
    }
}

