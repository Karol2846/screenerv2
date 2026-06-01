package com.stock.screener.collector.domain.service;

import com.stock.screener.collector.domain.kernel.CalculationGuard;
import com.stock.screener.collector.domain.kernel.CalculationResult;
import com.stock.screener.collector.domain.valueobject.AltmanZScore;
import com.stock.screener.common.Sector;
import com.stock.screener.collector.domain.valueobject.snapshot.FinancialDataSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domain service responsible for calculating Altman Z-Score.
 *
 * <p>Formula selection per Altman's published guidance (Morningstar sector taxonomy):
 * <ul>
 *   <li><b>Original Z (1968)</b> — 5-factor, includes asset turnover (T5); requires {@code revenueTTM}.
 *       Applied to manufacturing-like sectors: {@code INDUSTRIALS, MINING, CONSUMER_DISCRETIONARY}.</li>
 *   <li><b>Z'' (1995)</b> — 4-factor, drops T5 (asset turnover); suitable for non-manufacturers
 *       and asset-light businesses. Applied to: {@code TECHNOLOGY, HEALTHCARE, ENERGY,
 *       COMMUNICATION_SERVICES, CONSUMER_DEFENSIVE}.</li>
 *   <li><b>Skip</b> — Altman explicitly excludes regulated/depository capital structures:
 *       {@code FINANCE, REAL_ESTATE, UTILITIES, OTHER}.</li>
 * </ul>
 *
 * <p>This calculator only computes the numerical value (a fact). Thresholds/interpretation
 * are scoring-engine concerns (future work).
 */
public class AltmanScoreCalculator {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    // === Manufacturing coefficients (Original Z-Score) ===
    private static final BigDecimal MFG_COEF_A = new BigDecimal("1.2");
    private static final BigDecimal MFG_COEF_B = new BigDecimal("1.4");
    private static final BigDecimal MFG_COEF_C = new BigDecimal("3.3");
    private static final BigDecimal MFG_COEF_D = new BigDecimal("0.6");
    private static final BigDecimal MFG_COEF_E = new BigDecimal("1.0");

    // === Non-Manufacturing coefficients (Z''-Score) ===
    private static final BigDecimal NON_MFG_COEF_A = new BigDecimal("6.56");
    private static final BigDecimal NON_MFG_COEF_B = new BigDecimal("3.26");
    private static final BigDecimal NON_MFG_COEF_C = new BigDecimal("6.72");
    private static final BigDecimal NON_MFG_COEF_D = new BigDecimal("1.05");

    public static boolean isApplicable(Sector sector) {
        return switch (sector) {
            case INDUSTRIALS, MINING, CONSUMER_DISCRETIONARY,
                 TECHNOLOGY, HEALTHCARE, ENERGY, COMMUNICATION_SERVICES, CONSUMER_DEFENSIVE -> true;
            default -> false;
        };
    }

    public static CalculationResult<AltmanZScore> calculate(FinancialDataSnapshot snapshot, Sector sector) {
        if (!isApplicable(sector)) {
            return CalculationResult.skip("Altman Z-Score not applicable for %s sector".formatted(sector));
        }
        return switch (sector) {
            case INDUSTRIALS, MINING, CONSUMER_DISCRETIONARY -> calculateManufacturing(snapshot);
            case TECHNOLOGY, HEALTHCARE, ENERGY, COMMUNICATION_SERVICES, CONSUMER_DEFENSIVE ->
                    calculateNonManufacturing(snapshot);
            default -> throw new IllegalStateException("isApplicable/calculate out of sync for sector: " + sector);
        };
    }

    private static CalculationResult<AltmanZScore> calculateManufacturing(FinancialDataSnapshot snapshot) {
        return baseValidation(snapshot)
                .require("revenueTTM", FinancialDataSnapshot::revenueTTM)
                .validate(AltmanScoreCalculator::computeManufacturingScore);
    }

    private static CalculationResult<AltmanZScore> calculateNonManufacturing(FinancialDataSnapshot snapshot) {
        return baseValidation(snapshot)
                .validate(AltmanScoreCalculator::computeNonManufacturingScore);
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

    private static AltmanZScore computeManufacturingScore(FinancialDataSnapshot snapshot) {
        BigDecimal score = MFG_COEF_A.multiply(calculateT1(snapshot))
                .add(MFG_COEF_B.multiply(calculateT2(snapshot)))
                .add(MFG_COEF_C.multiply(calculateT3(snapshot)))
                .add(MFG_COEF_D.multiply(calculateT4(snapshot)))
                .add(MFG_COEF_E.multiply(calculateT5(snapshot)))
                .setScale(SCALE, ROUNDING);

        return new AltmanZScore(score);
    }

    private static AltmanZScore computeNonManufacturingScore(FinancialDataSnapshot snapshot) {
        BigDecimal score = NON_MFG_COEF_A.multiply(calculateT1(snapshot))
                .add(NON_MFG_COEF_B.multiply(calculateT2(snapshot)))
                .add(NON_MFG_COEF_C.multiply(calculateT3(snapshot)))
                .add(NON_MFG_COEF_D.multiply(calculateT4(snapshot)))
                .setScale(SCALE, ROUNDING);

        return new AltmanZScore(score);
    }

    // T1 = Working Capital / Total Assets
    private static BigDecimal calculateT1(FinancialDataSnapshot snapshot) {
        BigDecimal workingCapital = snapshot.totalCurrentAssets()
                .subtract(snapshot.totalCurrentLiabilities());
        return divide(workingCapital, snapshot.totalAssets());
    }

    // T2 = Retained Earnings / Total Assets
    private static BigDecimal calculateT2(FinancialDataSnapshot snapshot) {
        return divide(snapshot.retainedEarnings(), snapshot.totalAssets());
    }

    // T3 = EBIT / Total Assets
    private static BigDecimal calculateT3(FinancialDataSnapshot snapshot) {
        return divide(snapshot.ebit(), snapshot.totalAssets());
    }

    // T4 = Total Shareholder Equity / Total Liabilities
    private static BigDecimal calculateT4(FinancialDataSnapshot snapshot) {
        return divide(snapshot.totalShareholderEquity(), snapshot.totalLiabilities());
    }

    // T5 = Revenue TTM / Total Assets (only for manufacturing)
    private static BigDecimal calculateT5(FinancialDataSnapshot snapshot) {
        return divide(snapshot.revenueTTM(), snapshot.totalAssets());
    }

    private static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        return numerator.divide(denominator, SCALE, ROUNDING);
    }
}

