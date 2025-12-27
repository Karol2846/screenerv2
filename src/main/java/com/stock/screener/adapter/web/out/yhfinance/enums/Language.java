package com.stock.screener.adapter.web.out.yhfinance.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Jezyki obslugiwane przez YH Finance API.
 */
@Getter
@RequiredArgsConstructor
public enum Language {
    EN("en"),
    FR("fr"),
    DE("de"),
    IT("it"),
    ES("es"),
    ZH("zh");

    private final String value;
}

