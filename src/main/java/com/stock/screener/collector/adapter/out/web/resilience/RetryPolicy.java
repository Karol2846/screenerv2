package com.stock.screener.collector.adapter.out.web.resilience;

import java.time.Duration;

public record RetryPolicy(int maxAttempts, Duration baseBackoff, Duration maxBackoff, Duration jitter) {

    public RetryPolicy {
        if (maxAttempts < 0) {
            throw new IllegalArgumentException("maxAttempts must be >= 0");
        }
        if (baseBackoff == null || baseBackoff.isZero() || baseBackoff.isNegative()) {
            throw new IllegalArgumentException("baseBackoff must be a positive duration");
        }
        if (maxBackoff == null || maxBackoff.isZero() || maxBackoff.isNegative()) {
            throw new IllegalArgumentException("maxBackoff must be a positive duration");
        }
        if (maxBackoff.minus(baseBackoff).isNegative()) {
            throw new IllegalArgumentException("maxBackoff must be >= baseBackoff");
        }
        if (jitter == null || jitter.isNegative()) {
            throw new IllegalArgumentException("jitter must be >= 0");
        }
    }
}

