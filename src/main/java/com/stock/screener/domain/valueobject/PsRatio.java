package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record PsRatio(BigDecimal value) implements FinancialMetric {

    public static CalculationResult<PsRatio> compute(MarketDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("marketCap", MarketDataSnapshot::marketCap)
                .ensureNonZero("revenueTTM", MarketDataSnapshot::revenueTTM)
                .validate(PsRatio::calculateRatio);
    }

    private static PsRatio calculateRatio(MarketDataSnapshot snapshot) {
        BigDecimal result = FinancialMetric.divide(snapshot.marketCap(), snapshot.revenueTTM());
        return new PsRatio(result);
    }

    /**
     * @deprecated Use {@link #compute(MarketDataSnapshot)} instead
     */
    @Deprecated(forRemoval = true)
    public static PsRatio calculate(BigDecimal marketCap, BigDecimal revenueTTM) {
        if (FinancialMetric.isZeroOrNull(marketCap)) {
            return null;
        }
        BigDecimal result = FinancialMetric.divide(marketCap, revenueTTM);
        return result != null ? new PsRatio(result) : null;
    }
}

