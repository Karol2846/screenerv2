package com.stock.screener.collector.application.port.out.file;

/**
 * Exception thrown when there's an issue reading tickers
 * from the output port (e.g. missing file, I/O errors).
 */
public class TickerReaderException extends RuntimeException {

    public TickerReaderException(String message) {
        super(message);
    }

    public TickerReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
