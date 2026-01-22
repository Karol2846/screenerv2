package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationResult;
import io.smallrye.config.common.utils.StringUtil;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record QuickRatio(BigDecimal value) implements FinancialMetric {

    private static final String METRIC_NAME = "QuickRatio";

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

    /**
     * @deprecated Użyj {@link #compute} z obsługą Result Pattern.
     */
    @Deprecated(forRemoval = true)
    public static QuickRatio calculate(BigDecimal totalCurrentAssets,
                                       BigDecimal inventory,
                                       BigDecimal totalCurrentLiabilities) {
        return compute(totalCurrentAssets, inventory, totalCurrentLiabilities).getOrNull();
    }

    /**
     * Quick Ratio >= 1.0 oznacza zdrową płynność.
     */
    public boolean isHealthy() {
        return value != null && value.compareTo(BigDecimal.ONE) >= 0;
    }

    /**
     * Quick Ratio < 0.5 oznacza problemy z płynnością.
     */
    public boolean isCritical() {
        return value != null && value.compareTo(new BigDecimal("0.5")) < 0;
    }
}
