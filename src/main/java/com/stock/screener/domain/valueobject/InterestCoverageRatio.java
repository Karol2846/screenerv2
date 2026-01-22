package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationResult;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record InterestCoverageRatio(BigDecimal value) implements FinancialMetric {

    public static final String METRIC_NAME = "InterestCoverageRatio";

    public static CalculationResult<InterestCoverageRatio> compute(BigDecimal ebit,
                                                                    BigDecimal interestExpense) {
        if (ebit == null) {
            return CalculationResult.missingData("ebit");
        }
        if (interestExpense == null) {
            return CalculationResult.missingData("interestExpense");
        }
        if (interestExpense.compareTo(BigDecimal.ZERO) == 0) {
            return CalculationResult.divisionByZero("interestExpense");
        }

        BigDecimal result = FinancialMetric.divide(ebit, interestExpense);
        return CalculationResult.success(new InterestCoverageRatio(result));
    }

    /**
     * ICR >= 3.0 – firma komfortowo obsługuje dług.
     */
    public boolean isHealthy() {
        return value != null && value.compareTo(new BigDecimal("3.0")) >= 0;
    }

    /**
     * ICR < 1.5 – ryzyko niewypłacalności.
     */
    public boolean isCritical() {
        return value != null && value.compareTo(new BigDecimal("1.5")) < 0;
    }
}

