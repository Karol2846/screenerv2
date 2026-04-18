package com.stock.screener.collector.adapter.out.web.resilience;

import com.stock.screener.collector.adapter.out.web.alphavantage.exception.AlphaVantageApiException;
import com.stock.screener.collector.adapter.out.web.yhfinance.exception.YhFinanceApiException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Slf4j
@ApplicationScoped
public class ExternalApiCallExecutor {

    private final ProviderRateLimiter rateLimiter;
    private final ResiliencePolicyProperties policyProperties;

    ExternalApiCallExecutor(ProviderRateLimiter rateLimiter, ResiliencePolicyProperties policyProperties) {
        this.rateLimiter = rateLimiter;
        this.policyProperties = policyProperties;
    }

    public <T> T execute(ExternalProvider provider, String operation, Supplier<T> call) {
        ProviderRateLimitPolicy rateLimitPolicy = policyProperties.providerPolicy(provider);
        RetryPolicy retryPolicy = policyProperties.retryPolicy();

        int attempt = 1;
        while (true) {
            rateLimiter.acquire(provider, rateLimitPolicy);
            long startedAtNs = System.nanoTime();
            try {
                T result = call.get();
                long latencyMs = Duration.ofNanos(System.nanoTime() - startedAtNs).toMillis();
                log.debug("External call success provider={}, operation={}, attempt={}, latencyMs={}",
                        provider, operation, attempt, latencyMs);
                return result;
            } catch (RuntimeException ex) {
                long latencyMs = Duration.ofNanos(System.nanoTime() - startedAtNs).toMillis();
                if (!isRetryable(ex) || attempt > retryPolicy.maxAttempts()) {
                    log.warn("External call failed provider={}, operation={}, attempt={}, latencyMs={}, retryable={}",
                            provider, operation, attempt, latencyMs, isRetryable(ex));
                    throw ex;
                }

                Duration delay = computeBackoffDelay(attempt, retryPolicy);
                log.warn("External call retry provider={}, operation={}, attempt={}, latencyMs={}, delayMs={}",
                        provider, operation, attempt, latencyMs, delay.toMillis());

                sleep(delay, provider, operation, attempt);
                attempt++;
            }
        }
    }

    private static boolean isRetryable(RuntimeException exception) {
        if (exception instanceof YhFinanceApiException yhFinanceEx) {
            return yhFinanceEx.getStatusCode() == 429 || yhFinanceEx.getStatusCode() >= 500;
        }
        if (exception instanceof AlphaVantageApiException alphaVantageEx) {
            return alphaVantageEx.getStatusCode() == 429 || alphaVantageEx.getStatusCode() >= 500;
        }
        return exception instanceof ProcessingException;
    }

    private static Duration computeBackoffDelay(int attempt, RetryPolicy retryPolicy) {
        long baseMs = retryPolicy.baseBackoff().toMillis();
        long maxMs = retryPolicy.maxBackoff().toMillis();
        long jitterMaxMs = retryPolicy.jitter().toMillis();

        long exponentialMs = baseMs;
        for (int i = 1; i < attempt; i++) {
            if (exponentialMs >= maxMs) {
                exponentialMs = maxMs;
                break;
            }
            if (exponentialMs > maxMs / 2) {
                exponentialMs = maxMs;
            } else {
                exponentialMs = exponentialMs * 2;
            }
        }

        long cappedMs = Math.min(exponentialMs, maxMs);
        long jitterMs = jitterMaxMs > 0
                ? ThreadLocalRandom.current().nextLong(jitterMaxMs + 1)
                : 0;

        return Duration.ofMillis(cappedMs + jitterMs);
    }

    private static void sleep(Duration delay, ExternalProvider provider, String operation, int attempt) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted during external API backoff: provider=%s, operation=%s, attempt=%d"
                            .formatted(provider, operation, attempt),
                    interrupted);
        }
    }
}

