package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationResult;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

import static com.stock.screener.domain.valueobject.FinancialMetric.*;

@Embeddable
public record AltmanZScore(BigDecimal value) implements FinancialMetric {

    public static final String METRIC_NAME = "AltmanZScore";

    private static final BigDecimal COEF_T1 = new BigDecimal("6.56");
    private static final BigDecimal COEF_T2 = new BigDecimal("3.26");
    private static final BigDecimal COEF_T3 = new BigDecimal("6.72");
    private static final BigDecimal COEF_T4 = new BigDecimal("1.05");

    public static CalculationResult<AltmanZScore> compute(BigDecimal totalCurrentAssets,
                                                           BigDecimal totalCurrentLiabilities,
                                                           BigDecimal totalAssets,
                                                           BigDecimal retainedEarnings,
                                                           BigDecimal ebit,
                                                           BigDecimal totalShareholderEquity,
                                                           BigDecimal totalLiabilities) {
        // Walidacja wymaganych pól
        if (totalCurrentAssets == null) {
            return CalculationResult.missingData("totalCurrentAssets");
        }
        if (totalCurrentLiabilities == null) {
            return CalculationResult.missingData("totalCurrentLiabilities");
        }
        if (totalAssets == null) {
            return CalculationResult.missingData("totalAssets");
        }
        if (retainedEarnings == null) {
            return CalculationResult.missingData("retainedEarnings");
        }
        if (ebit == null) {
            return CalculationResult.missingData("ebit");
        }
        if (totalShareholderEquity == null) {
            return CalculationResult.missingData("totalShareholderEquity");
        }
        if (totalLiabilities == null) {
            return CalculationResult.missingData("totalLiabilities");
        }

        // Walidacja mianowników
        if (totalAssets.compareTo(BigDecimal.ZERO) == 0) {
            return CalculationResult.divisionByZero("totalAssets");
        }
        if (totalLiabilities.compareTo(BigDecimal.ZERO) == 0) {
            return CalculationResult.divisionByZero("totalLiabilities");
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

        BigDecimal score = COEF_T1.multiply(t1)
                .add(COEF_T2.multiply(t2))
                .add(COEF_T3.multiply(t3))
                .add(COEF_T4.multiply(t4))
                .setScale(SCALE, ROUNDING);

        return CalculationResult.success(new AltmanZScore(score));
    }
}

