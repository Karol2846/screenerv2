package com.stock.screener.collector.adapter.out.web.resilience;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

@ApplicationScoped
class ResiliencePolicyProperties {

    private final ProviderRateLimitPolicy yhFinancePolicy;
    private final ProviderRateLimitPolicy alphaVantagePolicy;
    private final RetryPolicy retryPolicy;

    ResiliencePolicyProperties(
            @ConfigProperty(name = "collector.resilience.yhfinance.requests-per-window") int yhRequestsPerWindow,
            @ConfigProperty(name = "collector.resilience.yhfinance.window-seconds") int yhWindowSeconds,
            @ConfigProperty(name = "collector.resilience.alphavantage.requests-per-window") int avRequestsPerWindow,
            @ConfigProperty(name = "collector.resilience.alphavantage.window-seconds") int avWindowSeconds,
            @ConfigProperty(name = "collector.resilience.retry.max-attempts") int maxRetryAttempts,
            @ConfigProperty(name = "collector.resilience.retry.base-backoff-millis") long baseBackoffMillis,
            @ConfigProperty(name = "collector.resilience.retry.max-backoff-millis") long maxBackoffMillis,
            @ConfigProperty(name = "collector.resilience.retry.jitter-millis") long jitterMillis) {
        this.yhFinancePolicy = new ProviderRateLimitPolicy(yhRequestsPerWindow, Duration.ofSeconds(yhWindowSeconds));
        this.alphaVantagePolicy = new ProviderRateLimitPolicy(avRequestsPerWindow, Duration.ofSeconds(avWindowSeconds));
        this.retryPolicy = new RetryPolicy(
                maxRetryAttempts,
                Duration.ofMillis(baseBackoffMillis),
                Duration.ofMillis(maxBackoffMillis),
                Duration.ofMillis(jitterMillis));
    }

    ProviderRateLimitPolicy providerPolicy(ExternalProvider provider) {
        return switch (provider) {
            case YH_FINANCE -> yhFinancePolicy;
            case ALPHA_VANTAGE -> alphaVantagePolicy;
        };
    }

    RetryPolicy retryPolicy() {
        return retryPolicy;
    }
}

