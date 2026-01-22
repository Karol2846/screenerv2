package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record InterestCoverageRatio(BigDecimal value) implements FinancialMetric {

    public static InterestCoverageRatio calculate(BigDecimal ebit, BigDecimal interestExpense) {
        BigDecimal result = FinancialMetric.divide(ebit, interestExpense);
        return result != null ? new InterestCoverageRatio(result) : null;
    }
}

