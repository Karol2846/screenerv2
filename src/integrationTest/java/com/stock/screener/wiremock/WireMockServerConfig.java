package com.stock.screener.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/**
 * JUnit 5 extension that manages a single, shared WireMock server lifecycle for
 * integration tests.
 *
 * Usage:
 * 
 * @ExtendWith(WireMockServerConfig.class)
 *                                         class MyIntegrationTest { ... }
 *
 *                                         The server is registered in the root
 *                                         ExtensionContext store with an
 *                                         AutoCloseable
 *                                         wrapper. This ensures it is shared
 *                                         across all test classes and is
 *                                         neither restarted
 *                                         nor stopped individually between them
 *                                         for maximum performance.
 *
 *                                         Use AlphaVantageWireMock or
 *                                         YhFinanceWireMock for programmatic
 *                                         stub registration.
 */
public class WireMockServerConfig implements BeforeAllCallback, AfterAllCallback {

    /** Port on which the WireMock server listens. */
    public static final int WIREMOCK_PORT = 8089;

    private static final Namespace NAMESPACE = Namespace.create(WireMockServerConfig.class);
    private static final String SERVER_KEY = "wireMockServer";

    @Override
    public void beforeAll(ExtensionContext context) {
        // Use the ROOT store so the server is shared across all test classes and
        // started only once. JUnit closes AutoCloseable values when the root store
        // is torn down (end of the entire test suite).
        context.getRoot()
                .getStore(NAMESPACE)
                .getOrComputeIfAbsent(SERVER_KEY, key -> {
                    WireMockServer server = new WireMockServer(
                            WireMockConfiguration.wireMockConfig()
                                    .port(WIREMOCK_PORT));
                    server.start();
                    WireMockHolder.INSTANCE = server;
                    // AutoCloseable lambda — JUnit 5 closes it automatically
                    return (AutoCloseable) server::stop;
                });
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // Intentionally empty: the server is stopped by JUnit when the root store
        // is closed, not after each individual test class.
    }

    /**
     * Returns the running WireMockServer instance for programmatic stub
     * registration.
     * 
     * @throws IllegalStateException if the server has not been started yet
     */
    public static WireMockServer getServer() {
        WireMockServer instance = WireMockHolder.INSTANCE;
        if (instance == null || !instance.isRunning()) {
            throw new IllegalStateException(
                    "WireMock server is not running. " +
                            "Ensure the test class is annotated with @ExtendWith(WireMockServerConfig.class).");
        }
        return instance;
    }

    /** Returns the port the WireMock server is bound to. */
    public static int getPort() {
        return WIREMOCK_PORT;
    }

    static final class WireMockHolder {
        static WireMockServer INSTANCE;

        private WireMockHolder() {
        }
    }
}
