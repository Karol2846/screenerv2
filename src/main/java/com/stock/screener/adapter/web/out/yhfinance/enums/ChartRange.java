package com.stock.screener.adapter.web.out.yhfinance.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Zakresy czasowe w Chart/Spark API.
 */
@Getter
@RequiredArgsConstructor
public enum ChartRange {
    ONE_DAY("1d"),
    FIVE_DAYS("5d"),
    ONE_MONTH("1mo"),
    THREE_MONTHS("3mo"),
    SIX_MONTHS("6mo"),
    ONE_YEAR("1y"),
    FIVE_YEARS("5y"),
    TEN_YEARS("10y"),
    YEAR_TO_DATE("ytd"),
    MAX("max");

    private final String value;
}

