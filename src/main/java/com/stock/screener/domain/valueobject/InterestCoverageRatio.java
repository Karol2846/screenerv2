package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshot.FinancialDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record InterestCoverageRatio(BigDecimal value) implements FinancialMetric {

    public static CalculationResult<InterestCoverageRatio> compute(FinancialDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("ebit", FinancialDataSnapshot::ebit)
                .ensureNonZero("interestExpense", FinancialDataSnapshot::interestExpense)
                .validate(InterestCoverageRatio::calculateRatio);
    }

    private static InterestCoverageRatio calculateRatio(FinancialDataSnapshot snapshot) {
        BigDecimal result = FinancialMetric.divide(snapshot.ebit(), snapshot.interestExpense());
        return new InterestCoverageRatio(result);
    }
}

