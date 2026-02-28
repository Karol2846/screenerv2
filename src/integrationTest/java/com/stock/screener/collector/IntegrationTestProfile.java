package com.stock.screener.collector;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class IntegrationTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.rest-client.alphavantage-api.url", "http://localhost:8089",
                "quarkus.rest-client.yhfinance-api.url", "http://localhost:8089",
                "alphavantage.api.key", "test-alphavantage-key",
                "alphavantage.api.base-url", "http://localhost:8089",
                "yhfinance.api.key", "test-yhfinance-key",
                "yhfinance.api.base-url", "http://localhost:8089",
                "quarkus.scheduler.enabled", "false");
    }
}
