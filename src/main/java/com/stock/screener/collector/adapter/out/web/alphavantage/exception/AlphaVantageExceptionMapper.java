package com.stock.screener.collector.adapter.out.web.alphavantage.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static com.stock.screener.collector.adapter.out.web.ResponseBodyReader.readResponseBody;

@Provider
public class AlphaVantageExceptionMapper implements ResponseExceptionMapper<AlphaVantageApiException> {

    @Override
    public AlphaVantageApiException toThrowable(Response response) {
        int status = response.getStatus();
        String body = readResponseBody(response);

        String message = switch (status) {
            case 401 -> "Unauthorized: Invalid or missing API key";
            case 403 -> "Forbidden: Access denied to Alpha Vantage API";
            case 404 -> "Not found: Symbol or endpoint not found";
            case 429 -> "Rate limit exceeded: Too many requests to Alpha Vantage API";
            case 500, 502, 503, 504 -> "External api throw unknown error";
            default -> "Alpha Vantage API error: HTTP " + status;
        };

        return new AlphaVantageApiException(message, status, body);
    }
}

