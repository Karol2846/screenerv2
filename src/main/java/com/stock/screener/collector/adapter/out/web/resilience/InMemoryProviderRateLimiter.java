package com.stock.screener.collector.adapter.out.web.resilience;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@ApplicationScoped
class InMemoryProviderRateLimiter implements ProviderRateLimiter {

    private final Map<ExternalProvider, WindowState> stateByProvider = new EnumMap<>(ExternalProvider.class);
    private final Map<ExternalProvider, Object> lockByProvider = new EnumMap<>(ExternalProvider.class);

    InMemoryProviderRateLimiter() {
        for (ExternalProvider provider : ExternalProvider.values()) {
            lockByProvider.put(provider, new Object());
        }
    }

    @Override
    public void acquire(ExternalProvider provider, ProviderRateLimitPolicy policy) {
        Object lock = lockByProvider.get(provider);
        while (true) {
            long waitMs;
            synchronized (lock) {
                Instant now = Instant.now();
                WindowState current = stateByProvider.computeIfAbsent(provider, ignored -> new WindowState(now, 0));

                if (Duration.between(current.windowStart(), now).compareTo(policy.window()) >= 0) {
                    current = new WindowState(now, 0);
                    stateByProvider.put(provider, current);
                }

                if (current.requestsInWindow() < policy.maxRequests()) {
                    stateByProvider.put(provider,
                            new WindowState(current.windowStart(), current.requestsInWindow() + 1));
                    return;
                }

                waitMs = policy.window().toMillis() - Duration.between(current.windowStart(), now).toMillis();
            }

            if (waitMs > 0) {
                log.info("Rate limit reached for provider={}, waiting {} ms before next request", provider, waitMs);
                sleep(waitMs, provider);
            }
        }
    }

    private static void sleep(long waitMs, ExternalProvider provider) {
        try {
            Thread.sleep(waitMs);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while waiting for rate-limit window reset for provider: " + provider, interrupted);
        }
    }

    private record WindowState(Instant windowStart, int requestsInWindow) {
    }
}

