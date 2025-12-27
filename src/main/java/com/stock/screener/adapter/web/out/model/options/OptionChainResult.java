package com.stock.screener.adapter.web.out.model.options;

import com.stock.screener.adapter.web.out.model.quote.Quote;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wynik dla pojedynczego symbolu w option chain.
 */
@Builder
@Jacksonized
public record OptionChainResult(
        String underlyingSymbol,
        List<Long> expirationDates,
        List<Double> strikes,
        Boolean hasMiniOptions,
        Quote quote,
        List<OptionData> options
) {
}

