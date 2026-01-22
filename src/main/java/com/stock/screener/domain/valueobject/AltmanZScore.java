package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

import static com.stock.screener.domain.valueobject.FinancialMetric.*;

@Embeddable
public record AltmanZScore(BigDecimal value) implements FinancialMetric {

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
        BigDecimal workingCapital = totalCurrentAssets.subtract(totalCurrentLiabilities);

        // T1 = Working Capital / Total Assets
        BigDecimal t1 = divide(workingCapital, totalAssets);

        // T2 = Retained Earnings / Total Assets
        BigDecimal t2 = divide(retainedEarnings, totalAssets);

        // T3 = EBIT / Total Assets
        BigDecimal t3 = divide(ebit, totalAssets);

        // T4 = Total Shareholder Equity / Total Liabilities
        BigDecimal t4 = divide(totalShareholderEquity, totalLiabilities);

        if (t1 == null || t2 == null || t3 == null || t4 == null) {
            return null;
        }

        BigDecimal score = COEF_T1.multiply(t1)
                .add(COEF_T2.multiply(t2))
                .add(COEF_T3.multiply(t3))
                .add(COEF_T4.multiply(t4))
                .setScale(SCALE, ROUNDING);

        return new AltmanZScore(score);
    }
}

