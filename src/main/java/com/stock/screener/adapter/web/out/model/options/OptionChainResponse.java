package com.stock.screener.adapter.web.out.model.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v7/finance/options/{symbol}
 */
@Builder
@Jacksonized
public record OptionChainResponse(
        @JsonProperty("optionChain")
        OptionChainBody optionChain
) {

    @Builder
    @Jacksonized
    public record OptionChainBody(
            List<OptionChainResult> result,
            String error
    ) {
    }
}

