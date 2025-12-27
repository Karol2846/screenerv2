package com.stock.screener.adapter.web.out.model.options;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Kontrakt opcyjny (call lub put).
 */
@Builder
@Jacksonized
public record OptionContract(
        String contractSymbol,
        String contractSize,
        String currency,
        Double strike,
        Long expiration,
        Long lastTradeDate,
        Double lastPrice,
        Double ask,
        Double bid,
        Double change,
        Double percentChange,
        Long volume,
        Long openInterest,
        Double impliedVolatility,
        Boolean inTheMoney
) {
}

