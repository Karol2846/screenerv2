package com.stock.screener.adapter.web.out.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Reprezentuje wartość z formatem (np. raw: 1.28, fmt: "1.28")
 * Używane powszechnie w odpowiedziach YH Finance API.
 */
@Builder
@Jacksonized
public record FormattedValue(
        Double raw,
        String fmt,
        String longFmt
) {
}

