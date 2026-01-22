package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record QuickRatio(BigDecimal value) implements FinancialMetric {

    public static QuickRatio calculate(BigDecimal totalCurrentAssets,
                                       BigDecimal inventory,
                                       BigDecimal totalCurrentLiabilities) {
        if (totalCurrentAssets == null || FinancialMetric.isZeroOrNull(totalCurrentLiabilities)) {
            return null;
        }
        BigDecimal inv = inventory != null ? inventory : BigDecimal.ZERO;
        BigDecimal numerator = totalCurrentAssets.subtract(inv);
        BigDecimal result = FinancialMetric.divide(numerator, totalCurrentLiabilities);
        return result != null ? new QuickRatio(result) : null;
    }

    public boolean isHealthy() {
        return value != null && value.compareTo(BigDecimal.ONE) >= 0;
    }

    public boolean isCritical() {
        return value != null && value.compareTo(new BigDecimal("0.5")) < 0;
    }
}
