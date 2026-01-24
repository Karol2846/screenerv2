package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

import static com.stock.screener.domain.valueobject.FinancialMetric.*;

@Embeddable
public record AltmanZScore(BigDecimal value) implements FinancialMetric {

    private static final BigDecimal COEF_T1 = new BigDecimal("6.56");
    private static final BigDecimal COEF_T2 = new BigDecimal("3.26");
    private static final BigDecimal COEF_T3 = new BigDecimal("6.72");
    private static final BigDecimal COEF_T4 = new BigDecimal("1.05");

    public static CalculationResult<AltmanZScore> compute(FinancialDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("totalCurrentAssets", FinancialDataSnapshot::totalCurrentAssets)
                .require("totalCurrentLiabilities", FinancialDataSnapshot::totalCurrentLiabilities)
                .require("retainedEarnings", FinancialDataSnapshot::retainedEarnings)
                .require("ebit", FinancialDataSnapshot::ebit)
                .require("totalShareholderEquity", FinancialDataSnapshot::totalShareholderEquity)
                .ensureNonZero("totalAssets", FinancialDataSnapshot::totalAssets)
                .ensureNonZero("totalLiabilities", FinancialDataSnapshot::totalLiabilities)
                .validate(AltmanZScore::calculateScore);
    }

    private static AltmanZScore calculateScore(FinancialDataSnapshot snapshot) {
        BigDecimal workingCapital = snapshot.totalCurrentAssets()
                .subtract(snapshot.totalCurrentLiabilities());

        // T1 = Working Capital / Total Assets
        BigDecimal t1 = divide(workingCapital, snapshot.totalAssets());

        // T2 = Retained Earnings / Total Assets
        BigDecimal t2 = divide(snapshot.retainedEarnings(), snapshot.totalAssets());

        // T3 = EBIT / Total Assets
        BigDecimal t3 = divide(snapshot.ebit(), snapshot.totalAssets());

        // T4 = Total Shareholder Equity / Total Liabilities
        BigDecimal t4 = divide(snapshot.totalShareholderEquity(), snapshot.totalLiabilities());

        BigDecimal score = COEF_T1.multiply(t1)
                .add(COEF_T2.multiply(t2))
                .add(COEF_T3.multiply(t3))
                .add(COEF_T4.multiply(t4))
                .setScale(SCALE, ROUNDING);

        return new AltmanZScore(score);
    }
}

