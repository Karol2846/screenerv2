package com.stock.screener.adapter.web.out.yhfinance.client;

import lombok.Getter;

/**
 * Wyjątek reprezentujący błąd komunikacji z YH Finance API.
 */
@Getter
public class YhFinanceApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public YhFinanceApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}

