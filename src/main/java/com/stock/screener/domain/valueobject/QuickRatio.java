package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationResult;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record QuickRatio(BigDecimal value) implements FinancialMetric {

    public static final String METRIC_NAME = "QuickRatio";

    public static CalculationResult<QuickRatio> compute(BigDecimal totalCurrentAssets,
                                                        BigDecimal inventory,
                                                        BigDecimal totalCurrentLiabilities) {
        if (totalCurrentAssets == null) {
            return CalculationResult.missingData("totalCurrentAssets");
        }
        if (totalCurrentLiabilities == null) {
            return CalculationResult.missingData("totalCurrentLiabilities");
        }
        if (totalCurrentLiabilities.compareTo(BigDecimal.ZERO) == 0) {
            return CalculationResult.divisionByZero("totalCurrentLiabilities");
        }

        BigDecimal inv = inventory != null ? inventory : BigDecimal.ZERO;
        BigDecimal numerator = totalCurrentAssets.subtract(inv);
        BigDecimal result = FinancialMetric.divide(numerator, totalCurrentLiabilities);

        return CalculationResult.success(new QuickRatio(result));
    }
}
