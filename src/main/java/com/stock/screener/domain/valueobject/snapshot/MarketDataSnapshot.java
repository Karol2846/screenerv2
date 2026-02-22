package com.stock.screener.domain.valueobject.snapshot;

import com.stock.screener.domain.valueobject.AnalystRatings;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MarketDataSnapshot(
        BigDecimal currentPrice,
        BigDecimal marketCap,
        BigDecimal revenueTTM,
        BigDecimal forwardPeRatio,
        BigDecimal pegRatio,
        BigDecimal targetPrice,

        // === Yahoo Finance ===
        BigDecimal forwardEpsGrowth,
        BigDecimal forwardRevenueGrowth,
        AnalystRatings analystRatings
) {
}
