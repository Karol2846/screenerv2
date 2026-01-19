package com.stock.screener.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ForwardPeg(BigDecimal value) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public static ForwardPeg calculate(BigDecimal forwardPe, BigDecimal epsGrowthPercent) {
        if (forwardPe == null || isZeroOrNull(epsGrowthPercent)) {
            return null;
        }
        return new ForwardPeg(forwardPe.divide(epsGrowthPercent, SCALE, ROUNDING));
    }

    private static boolean isZeroOrNull(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }
}

