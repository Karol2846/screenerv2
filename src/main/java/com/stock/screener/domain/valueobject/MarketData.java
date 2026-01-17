package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Embeddable
public record MarketData(
        BigDecimal currentPrice,
        BigDecimal marketCap,
        BigDecimal forwardPeRatio,
        BigDecimal psRatio,
        BigDecimal forwardPegRatio,
        LocalDateTime lastUpdated
) {}