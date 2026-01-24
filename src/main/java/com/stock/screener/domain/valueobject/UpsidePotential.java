package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
public record UpsidePotential(BigDecimal value) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public static CalculationResult<UpsidePotential> compute(MarketDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("targetPrice", MarketDataSnapshot::targetPrice)
                .ensureNonZero("currentPrice", MarketDataSnapshot::currentPrice)
                .validate(UpsidePotential::calculateUpside);
    }

    private static UpsidePotential calculateUpside(MarketDataSnapshot snapshot) {
        BigDecimal upside = snapshot.targetPrice().subtract(snapshot.currentPrice())
                .divide(snapshot.currentPrice(), SCALE, ROUNDING)
                .multiply(HUNDRED);
        return new UpsidePotential(upside);
    }
}

