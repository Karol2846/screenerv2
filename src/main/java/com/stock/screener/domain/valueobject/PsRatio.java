package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
public record PsRatio(BigDecimal value) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public static PsRatio calculate(BigDecimal marketCap, BigDecimal revenueTTM) {
        if (isZeroOrNull(marketCap) || isZeroOrNull(revenueTTM)) {
            return null;
        }
        return new PsRatio(marketCap.divide(revenueTTM, SCALE, ROUNDING));
    }

    private static boolean isZeroOrNull(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }
}

