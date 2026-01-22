package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
public record InterestCoverageRatio(BigDecimal value) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public static InterestCoverageRatio calculate(BigDecimal ebit, BigDecimal interestExpense) {
        if (ebit == null || isZeroOrNull(interestExpense)) {
            return null;
        }
        return new InterestCoverageRatio(ebit.divide(interestExpense, SCALE, ROUNDING));
    }

    private static boolean isZeroOrNull(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }
}

