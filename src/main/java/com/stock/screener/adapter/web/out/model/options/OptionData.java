package com.stock.screener.adapter.web.out.model.options;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Dane opcji dla konkretnej daty wygaśnięcia.
 */
@Builder
@Jacksonized
public record OptionData(
        Long expirationDate,
        Boolean hasMiniOptions,
        List<OptionContract> calls,
        List<OptionContract> puts
) {
}

