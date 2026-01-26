package com.stock.screener.domain.valueobject.snapshoot;

import com.stock.screener.domain.valueobject.AnalystRatings;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MarketDataSnapshot(
        // === Alpha Vantage (AV) ===
        BigDecimal currentPrice,
        BigDecimal marketCap,
        BigDecimal revenueTTM,
        BigDecimal forwardPeRatio,
        BigDecimal targetPrice,

        // === Yahoo Finance (YH) ===
        BigDecimal forwardEpsGrowth,
        BigDecimal forwardRevenueGrowth,
        AnalystRatings analystRatings
) {
}

