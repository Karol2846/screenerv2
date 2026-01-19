package com.stock.screener.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record UpsidePotential(BigDecimal percent) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public static UpsidePotential calculate(BigDecimal targetPrice, BigDecimal currentPrice) {
        if (targetPrice == null || isZeroOrNull(currentPrice)) {
            return null;
        }
        BigDecimal upside = targetPrice.subtract(currentPrice)
                .divide(currentPrice, SCALE, ROUNDING)
                .multiply(HUNDRED);
        return new UpsidePotential(upside);
    }

    private static boolean isZeroOrNull(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }
}

