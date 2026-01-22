package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
public record AltmanZScore(BigDecimal value) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private static final BigDecimal COEF_T1 = new BigDecimal("6.56");
    private static final BigDecimal COEF_T2 = new BigDecimal("3.26");
    private static final BigDecimal COEF_T3 = new BigDecimal("6.72");
    private static final BigDecimal COEF_T4 = new BigDecimal("1.05");

    public static AltmanZScore calculate(BigDecimal totalCurrentAssets,
                                         BigDecimal totalCurrentLiabilities,
                                         BigDecimal totalAssets,
                                         BigDecimal retainedEarnings,
                                         BigDecimal ebit,
                                         BigDecimal totalShareholderEquity,
                                         BigDecimal totalLiabilities) {
        if (isZeroOrNull(totalAssets) || isZeroOrNull(totalLiabilities)) {
            return null;
        }
        if (totalCurrentAssets == null || totalCurrentLiabilities == null ||
            retainedEarnings == null || ebit == null || totalShareholderEquity == null) {
            return null;
        }

        // T1 = Working Capital / Total Assets
        BigDecimal workingCapital = totalCurrentAssets.subtract(totalCurrentLiabilities);
        BigDecimal t1 = workingCapital.divide(totalAssets, SCALE, ROUNDING);

        // T2 = Retained Earnings / Total Assets
        BigDecimal t2 = retainedEarnings.divide(totalAssets, SCALE, ROUNDING);

        // T3 = EBIT / Total Assets
        BigDecimal t3 = ebit.divide(totalAssets, SCALE, ROUNDING);

        // T4 = Total Shareholder Equity / Total Liabilities
        BigDecimal t4 = totalShareholderEquity.divide(totalLiabilities, SCALE, ROUNDING);

        BigDecimal score = COEF_T1.multiply(t1)
                .add(COEF_T2.multiply(t2))
                .add(COEF_T3.multiply(t3))
                .add(COEF_T4.multiply(t4))
                .setScale(SCALE, ROUNDING);

        return new AltmanZScore(score);
    }

    private static boolean isZeroOrNull(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }
}

