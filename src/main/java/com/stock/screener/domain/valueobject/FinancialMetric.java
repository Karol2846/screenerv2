package com.stock.screener.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

sealed interface FinancialMetric
        permits AltmanZScore, ForwardPeg, InterestCoverageRatio, PsRatio, QuickRatio {

    int SCALE = 4;
    RoundingMode ROUNDING = RoundingMode.HALF_UP;

    BigDecimal value();

    static boolean isZeroOrNull(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }

    static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        if (numerator == null || isZeroOrNull(denominator)) {
            return null;
        }
        return numerator.divide(denominator, SCALE, ROUNDING);
    }
}

