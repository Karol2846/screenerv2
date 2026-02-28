package com.stock.screener.wiremock;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Fast utility for loading static WireMock JSON bodies from the classpath.
 */
final class StubFileReader {

    private static final String STUBS_ROOT = "stubs/__files/";

    private StubFileReader() {
    }

    static String read(String filename) {
        try (InputStream is = StubFileReader.class.getClassLoader().getResourceAsStream(STUBS_ROOT + filename)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read stub: " + filename, e);
        }
    }
}
