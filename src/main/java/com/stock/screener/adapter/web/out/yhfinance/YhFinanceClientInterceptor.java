package com.stock.screener.adapter.web.out.yhfinance;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor dla YH Finance REST Client.
 *
 * Odpowiada za:
 * - Dodawanie API Key do kazdego requestu
 * - Logowanie requestow i odpowiedzi
 * - Obsluge bledow HTTP (4xx, 5xx)
 */
@Slf4j
@Provider
@RequiredArgsConstructor
public class YhFinanceClientInterceptor implements ClientRequestFilter, ClientResponseFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final YhFinanceApiProperties apiProperties;

    @Override
    public void filter(ClientRequestContext requestContext) {
        // Dodaj API Key
        requestContext.getHeaders().add(API_KEY_HEADER, apiProperties.key());

        log.debug("YH Finance API Request: {} {}",
                requestContext.getMethod(),
                requestContext.getUri());
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        int status = responseContext.getStatus();

        if (status >= 400) {
            String body = readBody(responseContext);
            String message = buildErrorMessage(status, body);

            log.error("YH Finance API Error: status={}, uri={}, body={}",
                    status, requestContext.getUri(), body);

            throw new YhFinanceApiException(message, status);
        }

        log.debug("YH Finance API Response: {} - status {}",
                requestContext.getUri(), status);
    }

    private String readBody(ClientResponseContext responseContext) {
        if (!responseContext.hasEntity()) {
            return "";
        }

        try {
            byte[] bytes = responseContext.getEntityStream().readAllBytes();
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.debug("Failed to read error response body", e);
            return "";
        }
    }

    private String buildErrorMessage(int status, String body) {
        return switch (status) {
            case 400 -> "Bad Request: Invalid parameters. " + body;
            case 401 -> "Unauthorized: Invalid or missing API key.";
            case 403 -> "Forbidden: Access denied.";
            case 404 -> "Not Found: Resource does not exist.";
            case 429 -> "Rate Limit Exceeded: Too many requests.";
            case 500 -> "Internal Server Error.";
            case 502 -> "Bad Gateway: API temporarily unavailable.";
            case 503 -> "Service Unavailable: API under maintenance.";
            case 504 -> "Gateway Timeout: API did not respond in time.";
            default -> String.format("API error (HTTP %d): %s", status, body);
        };
    }
}

