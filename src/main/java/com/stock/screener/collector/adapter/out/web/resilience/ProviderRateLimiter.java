package com.stock.screener.collector.adapter.out.web.resilience;

public interface ProviderRateLimiter {

    void acquire(ExternalProvider provider, ProviderRateLimitPolicy policy);
}

