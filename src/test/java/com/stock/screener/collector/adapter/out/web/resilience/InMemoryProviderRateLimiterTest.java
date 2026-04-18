package com.stock.screener.collector.adapter.out.web.resilience;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InMemoryProviderRateLimiter Tests")
class InMemoryProviderRateLimiterTest {

    private final InMemoryProviderRateLimiter limiter = new InMemoryProviderRateLimiter();

    @Test
    @DisplayName("Allows requests within configured window limit without waiting")
    void allowsRequestsWithinWindowLimit() {
        ProviderRateLimitPolicy policy = new ProviderRateLimitPolicy(2, Duration.ofMillis(200));

        long startedAtNs = System.nanoTime();
        limiter.acquire(ExternalProvider.YH_FINANCE, policy);
        limiter.acquire(ExternalProvider.YH_FINANCE, policy);
        long elapsedMs = Duration.ofNanos(System.nanoTime() - startedAtNs).toMillis();

        assertThat(elapsedMs).isLessThan(80);
    }

    @Test
    @DisplayName("Waits for window reset when limit is exhausted")
    void waitsForWindowResetWhenLimitExhausted() {
        ProviderRateLimitPolicy policy = new ProviderRateLimitPolicy(1, Duration.ofMillis(120));

        limiter.acquire(ExternalProvider.ALPHA_VANTAGE, policy);

        long startedAtNs = System.nanoTime();
        limiter.acquire(ExternalProvider.ALPHA_VANTAGE, policy);
        long elapsedMs = Duration.ofNanos(System.nanoTime() - startedAtNs).toMillis();

        assertThat(elapsedMs).isGreaterThanOrEqualTo(100);
    }
}

