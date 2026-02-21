package com.stock.screener.collector.adapter.out.web.yhfinance.exception;

import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Provider
public class YhFinanceExceptionMapper implements ResponseExceptionMapper<YhFinanceApiException> {

    @Override
    public YhFinanceApiException toThrowable(Response response) {
        int status = response.getStatus();
        String body = readResponseBody(response);

        String message = switch (status) {
            case 401 -> "Unauthorized: Invalid or missing API key";
            case 403 -> "Forbidden: Access denied to YH Finance API";
            case 404 -> "Not found: Symbol or endpoint not found";
            case 429 -> "Rate limit exceeded: Too many requests to YH Finance API";
            case 500, 502, 503, 504 -> "YH Finance API server error";
            default -> "YH Finance API error: HTTP " + status;
        };

        return new YhFinanceApiException(message, status, body);
    }

    private String readResponseBody(Response response) {
        try {
            Object entity = response.getEntity();
            if (entity instanceof InputStream inputStream) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            }
            return entity != null ? entity.toString() : "";
        } catch (IOException e) {
            return "Unable to read response body";
        }
    }
}

