package com.stock.screener.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

/**
 * Quarkus test resource that manages the WireMock server lifecycle for integration tests.
 *
 * <p>WireMock starts before the Quarkus application boots, so REST client URLs are
 * configured correctly via the overrides returned by {@link #start()}. Stub mappings
 * and response bodies are loaded automatically from the classpath directory
 * {@code stubs/} (see {@code src/integrationTest/resources/stubs/}).
 *
 * <p>Usage:
 * <pre>{@code
 * @QuarkusTest
 * @QuarkusTestResource(WireMockTestResource.class)
 * class MyIntegrationTest {
 *
 *     WireMockServer wireMock; // injected automatically by type
 * }
 * }</pre>
 *
 * <p>The running instance is also accessible via {@link #getServer()} for use in
 * static {@code @BeforeAll} methods where field injection is unavailable.
 */
public class WireMockTestResource implements QuarkusTestResourceLifecycleManager {

    private static WireMockServer server;

    /**
     * Returns the running {@link WireMockServer} instance.
     *
     * <p>Prefer using the injected field where possible. This method exists for
     * static {@code @BeforeAll} methods that cannot access instance fields.
     *
     * @throws IllegalStateException if the server has not been started yet
     */
    public static WireMockServer getServer() {
        if (server == null || !server.isRunning()) {
            throw new IllegalStateException(
                    "WireMock server is not running. "
                            + "Ensure the test class is annotated with @QuarkusTestResource(WireMockTestResource.class).");
        }
        return server;
    }

    @Override
    public Map<String, String> start() {
        server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .dynamicPort()
                        .usingFilesUnderClasspath("stubs"));
        server.start();

        String url = server.baseUrl();
        return Map.of(
                "quarkus.rest-client.alphavantage-api.url", url,
                "quarkus.rest-client.yhfinance-api.url", url,
                "alphavantage.api.base-url", url,
                "alphavantage.api.key", "test-alphavantage-key",
                "yhfinance.api.base-url", url,
                "yhfinance.api.key", "test-yhfinance-key");
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Injects the {@link WireMockServer} instance into any field of that type in the test class,
     * allowing tests to register additional stubs or verify recorded requests.
     */
    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(server, new TestInjector.MatchesType(WireMockServer.class));
    }
}
