package com.stock.screener.collector.domain.valueobject;

import com.stock.screener.collector.domain.kernel.CalculationGuard;
import com.stock.screener.collector.domain.kernel.CalculationResult;
import com.stock.screener.collector.domain.valueobject.snapshot.MarketDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record UpsidePotential(BigDecimal value) implements FinancialMetric {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public static CalculationResult<UpsidePotential> compute(MarketDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("targetPrice", MarketDataSnapshot::targetPrice)
                .ensureNonZero("currentPrice", MarketDataSnapshot::currentPrice)
                .validate(UpsidePotential::calculateUpside);
    }

    private static UpsidePotential calculateUpside(MarketDataSnapshot snapshot) {
        var numerator = snapshot.targetPrice().subtract(snapshot.currentPrice());
        var result = FinancialMetric.divide(numerator, snapshot.currentPrice());
        return new UpsidePotential(result.multiply(HUNDRED));
    }
}

