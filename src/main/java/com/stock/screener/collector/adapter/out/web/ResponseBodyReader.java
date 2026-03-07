package com.stock.screener.collector.adapter.out.web;

import jakarta.ws.rs.core.Response;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@UtilityClass
public class ResponseBodyReader {

    public static String readResponseBody(Response response) {
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
