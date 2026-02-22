package com.stock.screener.domain.service;

import com.stock.screener.domain.kernel.CalculationGuard;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.AltmanZScore;
import com.stock.screener.domain.valueobject.Sector;
import com.stock.screener.domain.valueobject.snapshot.FinancialDataSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domain service responsible for calculating Altman Z-Score.
 * <p>
 * Supports two formulas:
 * <ul>
 *   <li>Original Z-Score for manufacturing sectors (ENERGY, MINING, UTILITIES)</li>
 *   <li>Z''-Score for non-manufacturing sectors (TECHNOLOGY, HEALTHCARE, CONSUMER_DISCRETIONARY, REAL_ESTATE)</li>
 * </ul>
 * <p>
 * This calculator only computes the numerical value (a fact).
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

    /**
     * Calculates Altman Z-Score for the given financial snapshot and sector.
     *
     * @param snapshot financial data required for calculation
     * @param sector   company sector determining which formula to use
     * @return calculation result containing the Z-Score value or error information
     */
    public static CalculationResult<AltmanZScore> calculate(FinancialDataSnapshot snapshot, Sector sector) {
        return switch (sector) {
            case ENERGY, MINING, UTILITIES -> calculateManufacturing(snapshot);
            case TECHNOLOGY, HEALTHCARE, CONSUMER_DISCRETIONARY, REAL_ESTATE -> calculateNonManufacturing(snapshot);
            default -> CalculationResult.skip("Altman Z-Score not applicable for %s sector".formatted(sector));
        };
    }

    private static CalculationResult<AltmanZScore> calculateManufacturing(FinancialDataSnapshot snapshot) {
        return baseValidation(snapshot)
                .require("totalRevenue", FinancialDataSnapshot::totalRevenue)
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

    // T5 = Total Revenue / Total Assets (only for manufacturing)
    private static BigDecimal calculateT5(FinancialDataSnapshot snapshot) {
        return divide(snapshot.totalRevenue(), snapshot.totalAssets());
    }

    private static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        return numerator.divide(denominator, SCALE, ROUNDING);
    }
}

