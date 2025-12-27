package com.stock.screener.adapter.web.out.yhfinance.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Interwaly danych w Chart/Spark API.
 */
@Getter
@RequiredArgsConstructor
public enum ChartInterval {
    ONE_MINUTE("1m"),
    FIVE_MINUTES("5m"),
    FIFTEEN_MINUTES("15m"),
    ONE_DAY("1d"),
    ONE_WEEK("1wk"),
    ONE_MONTH("1mo");

    private final String value;
}

