package com.stock.screener.collector.adapter.out.web.resilience;

import com.stock.screener.collector.adapter.out.web.yhfinance.exception.YhFinanceApiException;
import jakarta.ws.rs.ProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExternalApiCallExecutor Tests")
class ExternalApiCallExecutorTest {

    @Test
    @DisplayName("Retries on HTTP 429 and eventually returns result")
    void retriesOn429AndEventuallySucceeds() {
        CountingRateLimiter rateLimiter = new CountingRateLimiter();
        ExternalApiCallExecutor executor = new ExternalApiCallExecutor(rateLimiter, testPolicies(2, 1, 5, 0));
        AtomicInteger attempts = new AtomicInteger(0);

        String result = executor.execute(ExternalProvider.YH_FINANCE, "quoteSummary:META", () -> {
            int callNo = attempts.incrementAndGet();
            if (callNo < 3) {
                throw new YhFinanceApiException("rate limited", 429, "{}");
            }
            return "ok";
        });

        assertThat(result).isEqualTo("ok");
        assertThat(attempts.get()).isEqualTo(3);
        assertThat(rateLimiter.invocations).isEqualTo(3);
    }

    @Test
    @DisplayName("Does not retry on non-retryable HTTP 404")
    void doesNotRetryOn404() {
        CountingRateLimiter rateLimiter = new CountingRateLimiter();
        ExternalApiCallExecutor executor = new ExternalApiCallExecutor(rateLimiter, testPolicies(2, 1, 5, 0));
        AtomicInteger attempts = new AtomicInteger(0);

        assertThatThrownBy(() -> executor.execute(ExternalProvider.YH_FINANCE, "quoteSummary:META", () -> {
            attempts.incrementAndGet();
            throw new YhFinanceApiException("not found", 404, "{}");
        })).isInstanceOf(YhFinanceApiException.class);

        assertThat(attempts.get()).isEqualTo(1);
        assertThat(rateLimiter.invocations).isEqualTo(1);
    }

    @Test
    @DisplayName("Retries on ProcessingException timeout and eventually succeeds")
    void retriesOnTimeoutProcessingException() {
        CountingRateLimiter rateLimiter = new CountingRateLimiter();
        ExternalApiCallExecutor executor = new ExternalApiCallExecutor(rateLimiter, testPolicies(1, 1, 5, 0));
        AtomicInteger attempts = new AtomicInteger(0);

        String result = executor.execute(ExternalProvider.ALPHA_VANTAGE, "fetchOverview:META", () -> {
            int callNo = attempts.incrementAndGet();
            if (callNo == 1) {
                throw new ProcessingException("timeout", new SocketTimeoutException("read timed out"));
            }
            return "ok";
        });

        assertThat(result).isEqualTo("ok");
        assertThat(attempts.get()).isEqualTo(2);
        assertThat(rateLimiter.invocations).isEqualTo(2);
    }

    @Test
    @DisplayName("Stops after configured retry attempts")
    void stopsAfterConfiguredRetries() {
        CountingRateLimiter rateLimiter = new CountingRateLimiter();
        ExternalApiCallExecutor executor = new ExternalApiCallExecutor(rateLimiter, testPolicies(2, 1, 5, 0));
        AtomicInteger attempts = new AtomicInteger(0);

        assertThatThrownBy(() -> executor.execute(ExternalProvider.YH_FINANCE, "quoteSummary:META", () -> {
            attempts.incrementAndGet();
            throw new YhFinanceApiException("unavailable", 503, "{}");
        })).isInstanceOf(YhFinanceApiException.class);

        assertThat(attempts.get()).isEqualTo(3);
        assertThat(rateLimiter.invocations).isEqualTo(3);
    }

    private static ResiliencePolicyProperties testPolicies(
            int maxRetries,
            long baseBackoffMs,
            long maxBackoffMs,
            long jitterMs) {
        return new ResiliencePolicyProperties(
                300,
                60,
                5,
                60,
                maxRetries,
                baseBackoffMs,
                maxBackoffMs,
                jitterMs);
    }

    private static final class CountingRateLimiter implements ProviderRateLimiter {
        int invocations = 0;

        @Override
        public void acquire(ExternalProvider provider, ProviderRateLimitPolicy policy) {
            invocations++;
        }
    }
}

