package com.stock.screener.adapter.web.out.yhfinance.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Identyfikatory predefiniowanych screenerow.
 */
@Getter
@RequiredArgsConstructor
public enum ScreenerId {
    DAY_GAINERS("day_gainers"),
    DAY_LOSERS("day_losers"),
    MOST_ACTIVES("most_actives"),
    UNDERVALUED_GROWTH_STOCKS("undervalued_growth_stocks"),
    GROWTH_TECHNOLOGY_STOCKS("growth_technology_stocks"),
    UNDERVALUED_LARGE_CAPS("undervalued_large_caps"),
    AGGRESSIVE_SMALL_CAPS("aggressive_small_caps"),
    SMALL_CAP_GAINERS("small_cap_gainers");

    private final String value;
}

