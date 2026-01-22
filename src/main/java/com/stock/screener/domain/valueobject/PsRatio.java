package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record PsRatio(BigDecimal value) implements FinancialMetric {

    public static PsRatio calculate(BigDecimal marketCap, BigDecimal revenueTTM) {
        if (FinancialMetric.isZeroOrNull(marketCap)) {
            return null;
        }
        BigDecimal result = FinancialMetric.divide(marketCap, revenueTTM);
        return result != null ? new PsRatio(result) : null;
    }
}

