package com.stock.screener.adapter.web.out.yhfinance.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Regiony gield obslugiwane przez YH Finance API.
 */
@Getter
@RequiredArgsConstructor
public enum Region {
    US("US"),
    AU("AU"),
    CA("CA"),
    FR("FR"),
    DE("DE"),
    HK("HK"),
    IT("IT"),
    ES("ES"),
    GB("GB"),
    IN("IN");

    private final String value;
}

