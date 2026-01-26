package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

import static com.stock.screener.domain.valueobject.FinancialMetric.*;

@Embeddable
public record AltmanZScore(BigDecimal value) implements FinancialMetric {

    // === Manufacturing constraints (Original Z-Score) ===
    private static final BigDecimal MFG_COEF_A = new BigDecimal("1.2");
    private static final BigDecimal MFG_COEF_B = new BigDecimal("1.4");
    private static final BigDecimal MFG_COEF_C = new BigDecimal("3.3");
    private static final BigDecimal MFG_COEF_D = new BigDecimal("0.6");
    private static final BigDecimal MFG_COEF_E = new BigDecimal("1.0");

    // === Non-Manufacturing constraints (Z''-Score) ===
    private static final BigDecimal NON_MFG_COEF_A = new BigDecimal("6.56");
    private static final BigDecimal NON_MFG_COEF_B = new BigDecimal("3.26");
    private static final BigDecimal NON_MFG_COEF_C = new BigDecimal("6.72");
    private static final BigDecimal NON_MFG_COEF_D = new BigDecimal("1.05");

    public static CalculationResult<AltmanZScore> compute(FinancialDataSnapshot snapshot, Sector sector) {
        return switch (sector) {
            case ENERGY, MINING, UTILITIES -> computeManufacturing(snapshot);
            case TECHNOLOGY, HEALTHCARE, CONSUMER_DISCRETIONARY, REAL_ESTATE -> computeNonManufacturing(snapshot);
            default -> CalculationResult.skip("Altman Z-Score not applicable for %s sector".formatted(sector));
        };
    }

    private static CalculationResult<AltmanZScore> computeManufacturing(FinancialDataSnapshot snapshot) {
        return baseValidation(snapshot)
                .require("totalRevenue", FinancialDataSnapshot::totalRevenue)
                .validate(AltmanZScore::calculateManufacturingScore);
    }

    private static CalculationResult<AltmanZScore> computeNonManufacturing(FinancialDataSnapshot snapshot) {
        return baseValidation(snapshot)
                .validate(AltmanZScore::calculateNonManufacturingScore);
    }

    private static CalculationGuard<FinancialDataSnapshot> baseValidation(FinancialDataSnapshot snapshot) {
        return CalculationGuard.check(snapshot)
                .require("totalCurrentAssets", FinancialDataSnapshot::totalCurrentAssets)
                .require("totalCurrentLiabilities", FinancialDataSnapshot::totalCurrentLiabilities)
                .require("retainedEarnings", FinancialDataSnapshot::retainedEarnings)
                .require("ebit", FinancialDataSnapshot::ebit)
                .require("totalShareholderEquity", FinancialDataSnapshot::totalShareholderEquity)
                .ensureNonZero("totalAssets", FinancialDataSnapshot::totalAssets)
                .ensureNonZero("totalLiabilities", FinancialDataSnapshot::totalLiabilities);
    }

    private static AltmanZScore calculateManufacturingScore(FinancialDataSnapshot snapshot) {
        BigDecimal score = MFG_COEF_A.multiply(calculateT1(snapshot))
                .add(MFG_COEF_B.multiply(calculateT2(snapshot)))
                .add(MFG_COEF_C.multiply(calculateT3(snapshot)))
                .add(MFG_COEF_D.multiply(calculateT4(snapshot)))
                .add(MFG_COEF_E.multiply(calculateT5(snapshot)))
                .setScale(SCALE, ROUNDING);

        return new AltmanZScore(score);
    }

    private static AltmanZScore calculateNonManufacturingScore(FinancialDataSnapshot snapshot) {
        BigDecimal score = NON_MFG_COEF_A.multiply(calculateT1(snapshot))
                .add(NON_MFG_COEF_B.multiply(calculateT2(snapshot)))
                .add(NON_MFG_COEF_C.multiply(calculateT3(snapshot)))
                .add(NON_MFG_COEF_D.multiply(calculateT4(snapshot)))
                .setScale(SCALE, ROUNDING);
        return new AltmanZScore(score);
    }

    private static BigDecimal calculateT1(FinancialDataSnapshot snapshot) {
        BigDecimal workingCapital = snapshot.totalCurrentAssets()
                .subtract(snapshot.totalCurrentLiabilities());
        return divide(workingCapital, snapshot.totalAssets());
    }

    private static BigDecimal calculateT2(FinancialDataSnapshot snapshot) {
        return divide(snapshot.retainedEarnings(), snapshot.totalAssets());
    }

    private static BigDecimal calculateT3(FinancialDataSnapshot snapshot) {
        return divide(snapshot.ebit(), snapshot.totalAssets());
    }

    private static BigDecimal calculateT4(FinancialDataSnapshot snapshot) {
        return divide(snapshot.totalShareholderEquity(), snapshot.totalLiabilities());
    }

    private static BigDecimal calculateT5(FinancialDataSnapshot snapshot) {
        return divide(snapshot.totalRevenue(), snapshot.totalAssets());
    }
}

