package com.stock.screener.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

sealed interface FinancialMetric
        permits AltmanZScore, ForwardPeg, InterestCoverageRatio, PsRatio, QuickRatio, UpsidePotential {

    int SCALE = 4;
    RoundingMode ROUNDING = RoundingMode.HALF_UP;

    BigDecimal value();

    static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        return numerator.divide(denominator, SCALE, ROUNDING);
    }
}

