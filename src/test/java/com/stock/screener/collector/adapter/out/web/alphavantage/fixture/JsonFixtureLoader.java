package com.stock.screener.collector.adapter.out.web.alphavantage.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public final class JsonFixtureLoader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String BASE_PATH = "alphavantage/";

    private JsonFixtureLoader() {}

    public static <T> T load(String fileName, Class<T> type) {
        String path = BASE_PATH + fileName;
        try (InputStream is = JsonFixtureLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Fixture not found: " + path);
            }
            return OBJECT_MAPPER.readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fixture: " + path, e);
        }
    }
}
