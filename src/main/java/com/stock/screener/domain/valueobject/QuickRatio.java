package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record QuickRatio(BigDecimal value) implements FinancialMetric {

    public static CalculationResult<QuickRatio> compute(FinancialDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("totalCurrentAssets", FinancialDataSnapshot::totalCurrentAssets)
                .ensureNonZero("totalCurrentLiabilities", FinancialDataSnapshot::totalCurrentLiabilities)
                .validate(QuickRatio::calculateRatio);
    }

    private static QuickRatio calculateRatio(FinancialDataSnapshot snapshot) {
        BigDecimal inventory = snapshot.inventory() != null
                ? snapshot.inventory()
                : BigDecimal.ZERO;

        BigDecimal numerator = snapshot.totalCurrentAssets().subtract(inventory);
        BigDecimal result = FinancialMetric.divide(numerator, snapshot.totalCurrentLiabilities());

        return new QuickRatio(result);
    }
}
