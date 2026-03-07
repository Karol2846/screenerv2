package com.stock.screener.wiremock;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Fast utility for loading static WireMock JSON bodies from the classpath.
 *
 * <p>Response body files live under {@code stubs/__files/} — the standard WireMock
 * convention for body files referenced by mapping descriptors in {@code stubs/mappings/}.
 */
final class StubFileReader {

    /** WireMock's conventional directory for response body files. */
    private static final String STUBS_ROOT = "stubs/__files/";

    private StubFileReader() {
    }

    static String read(String filename) {
        String path = STUBS_ROOT + filename;
        InputStream is = StubFileReader.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new RuntimeException("Stub file not found on classpath: " + path);
        }
        try (is) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read stub: " + filename, e);
        }
    }
}
