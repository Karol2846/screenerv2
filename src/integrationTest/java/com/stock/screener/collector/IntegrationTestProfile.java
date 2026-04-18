package com.stock.screener.collector;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class IntegrationTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.ofEntries(
                Map.entry("quarkus.rest-client.alphavantage-api.url", "http://localhost:8089"),
                Map.entry("quarkus.rest-client.yhfinance-api.url", "http://localhost:8089"),
                Map.entry("alphavantage.api.key", "test-alphavantage-key"),
                Map.entry("alphavantage.api.base-url", "http://localhost:8089"),
                Map.entry("yhfinance.api.key", "test-yhfinance-key"),
                Map.entry("yhfinance.api.base-url", "http://localhost:8089"),
                Map.entry("collector.resilience.yhfinance.requests-per-window", "1000"),
                Map.entry("collector.resilience.yhfinance.window-seconds", "60"),
                Map.entry("collector.resilience.alphavantage.requests-per-window", "1000"),
                Map.entry("collector.resilience.alphavantage.window-seconds", "60"),
                Map.entry("collector.resilience.retry.max-attempts", "1"),
                Map.entry("collector.resilience.retry.base-backoff-millis", "10"),
                Map.entry("collector.resilience.retry.max-backoff-millis", "10"),
                Map.entry("collector.resilience.retry.jitter-millis", "0"),
                Map.entry("quarkus.scheduler.enabled", "false"));
    }
}
