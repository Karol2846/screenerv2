package com.stock.screener.collector.adapter.out.web.resilience;

import java.time.Duration;

public record ProviderRateLimitPolicy(int maxRequests, Duration window) {

    public ProviderRateLimitPolicy {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be > 0");
        }
        if (window == null || window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("window must be a positive duration");
        }
    }
}

