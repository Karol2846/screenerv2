package com.stock.screener.adapter.web.out.yhfinance;

/**
 * Wyjatek rzucany podczas bledu komunikacji z YH Finance API.
 *
 * Zawiera szczegoly bledu takie jak:
 * - Kod HTTP odpowiedzi
 * - Komunikat bledu z API
 * - Oryginalny wyjatek (jesli dostepny)
 */
public class YhFinanceApiException extends RuntimeException {

    private final int statusCode;
    private final String apiErrorMessage;

    public YhFinanceApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.apiErrorMessage = message;
    }

    public YhFinanceApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.apiErrorMessage = message;
    }

    public YhFinanceApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.apiErrorMessage = message;
    }

    public YhFinanceApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.apiErrorMessage = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getApiErrorMessage() {
        return apiErrorMessage;
    }

    public boolean isClientError() {
        return statusCode >= 400 && statusCode < 500;
    }

    public boolean isServerError() {
        return statusCode >= 500;
    }

    public boolean isRateLimitExceeded() {
        return statusCode == 429;
    }

    public boolean isUnauthorized() {
        return statusCode == 401;
    }

    public boolean isNotFound() {
        return statusCode == 404;
    }

    @Override
    public String toString() {
        return String.format("YhFinanceApiException{statusCode=%d, message='%s'}",
                statusCode, apiErrorMessage);
    }
}

