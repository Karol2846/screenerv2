package com.stock.screener.domain.valueobject.snapshoot;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MarketDataSnapshot(
        BigDecimal currentPrice,
        BigDecimal marketCap,
        BigDecimal revenueTTM,
        BigDecimal forwardPeRatio,
        BigDecimal forwardEpsGrowth,
        BigDecimal targetPrice
) {
}

