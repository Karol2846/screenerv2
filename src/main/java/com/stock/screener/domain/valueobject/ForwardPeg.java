package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record ForwardPeg(BigDecimal value) implements FinancialMetric {

    public static ForwardPeg calculate(BigDecimal forwardPe, BigDecimal epsGrowthPercent) {
        BigDecimal result = FinancialMetric.divide(forwardPe, epsGrowthPercent);
        return result != null ? new ForwardPeg(result) : null;
    }
}

