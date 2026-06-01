package com.stock.screener.collector.adapter.out.web.alphavantage.exception;

public class ClientException extends RuntimeException {

    public ClientException(String message, Object... args) {
        super(String.format(message, args));
    }
}
