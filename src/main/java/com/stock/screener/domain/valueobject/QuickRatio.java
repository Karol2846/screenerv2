package com.stock.screener.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Quick Ratio = (totalCurrentAssets - inventory) / totalCurrentLiabilities
 * Measures short-term liquidity without relying on inventory.
 */
public record QuickRatio(BigDecimal value) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public static QuickRatio calculate(BigDecimal totalCurrentAssets,
                                       BigDecimal inventory,
                                       BigDecimal totalCurrentLiabilities) {
        if (totalCurrentAssets == null || isZeroOrNull(totalCurrentLiabilities)) {
            return null;
        }
        BigDecimal inv = inventory != null ? inventory : BigDecimal.ZERO;
        BigDecimal numerator = totalCurrentAssets.subtract(inv);
        return new QuickRatio(numerator.divide(totalCurrentLiabilities, SCALE, ROUNDING));
    }

    public boolean isHealthy() {
        return value != null && value.compareTo(BigDecimal.ONE) >= 0;
    }

    public boolean isCritical() {
        return value != null && value.compareTo(new BigDecimal("0.5")) < 0;
    }

    private static boolean isZeroOrNull(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }
}
