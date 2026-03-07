package com.stock.screener.collector.adapter.out.web.alphavantage.exception;

import lombok.Getter;

/**
 * Wyjątek reprezentujący błąd komunikacji z Alpha Vantage API.
 */
@Getter
public class AlphaVantageApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public AlphaVantageApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}

