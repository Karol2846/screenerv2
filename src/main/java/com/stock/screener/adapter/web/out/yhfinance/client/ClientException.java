package com.stock.screener.adapter.web.out.yhfinance.client;

public class ClientException extends RuntimeException {

    public ClientException(String message, Object... args) {
        super(String.format(message, args));
    }
}
