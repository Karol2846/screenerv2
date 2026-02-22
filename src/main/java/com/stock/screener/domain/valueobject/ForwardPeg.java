package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshot.MarketDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record ForwardPeg(BigDecimal value) implements FinancialMetric {

    public static CalculationResult<ForwardPeg> compute(MarketDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("forwardPeRatio", MarketDataSnapshot::forwardPeRatio)
                .ensureNonZero("forwardEpsGrowth", MarketDataSnapshot::forwardEpsGrowth)
                .validate(ForwardPeg::calculate);
    }

    private static ForwardPeg calculate(MarketDataSnapshot snapshot) {
        BigDecimal growthAsPercentage = snapshot.forwardEpsGrowth().multiply(new BigDecimal("100"));
        BigDecimal result = FinancialMetric.divide(snapshot.forwardPeRatio(), growthAsPercentage);
        return new ForwardPeg(result);
    }
}

