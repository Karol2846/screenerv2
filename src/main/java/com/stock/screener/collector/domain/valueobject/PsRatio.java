package com.stock.screener.collector.domain.valueobject;

import com.stock.screener.collector.domain.kernel.CalculationGuard;
import com.stock.screener.collector.domain.kernel.CalculationResult;
import com.stock.screener.collector.domain.valueobject.snapshot.MarketDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

import static com.stock.screener.collector.domain.valueobject.FinancialMetric.divide;

@Embeddable
public record PsRatio(BigDecimal value) implements FinancialMetric {

    public static CalculationResult<PsRatio> compute(MarketDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("marketCap", MarketDataSnapshot::marketCap)
                .ensureNonZero("revenueTTM", MarketDataSnapshot::revenueTTM)
                .validate(PsRatio::calculateRatio);
    }

    private static PsRatio calculateRatio(MarketDataSnapshot snapshot) {
        BigDecimal result = divide(snapshot.marketCap(), snapshot.revenueTTM());
        return new PsRatio(result);
    }
}

