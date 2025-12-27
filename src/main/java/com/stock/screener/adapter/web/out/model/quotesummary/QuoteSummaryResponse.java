package com.stock.screener.adapter.web.out.model.quotesummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Odpowiedź z endpointa /v11/finance/quoteSummary/{symbol}
 */
@Builder
@Jacksonized
public record QuoteSummaryResponse(
        @JsonProperty("quoteSummary")
        QuoteSummaryBody quoteSummary
) {

    @Builder
    @Jacksonized
    public record QuoteSummaryBody(
            List<QuoteSummaryResult> result,
            String error
    ) {
    }
}

