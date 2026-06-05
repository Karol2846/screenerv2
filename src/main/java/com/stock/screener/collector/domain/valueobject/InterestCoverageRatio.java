package com.stock.screener.collector.domain.valueobject;

import com.stock.screener.collector.domain.kernel.CalculationResult;
import com.stock.screener.collector.domain.valueobject.snapshot.FinancialDataSnapshot;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

/**
 * Interest Coverage Ratio — measures how many times EBIT covers interest expense.
 *
 * <p>Computation follows a 4-state classification:
 * <ol>
 *   <li>EBIT null → {@code Failure(MISSING_DATA)} — cannot classify at all.</li>
 *   <li>interestExpense null/0 → {@code Success(null, NO_DEBT)} — no debt service obligation; positive signal.</li>
 *   <li>EBIT &lt; 0, interest &gt; 0 → {@code Success(null, OPERATING_LOSS)} — ratio would be misleading.</li>
 *   <li>EBIT ≥ 0, interest &gt; 0 → {@code Success(ebit/interest, COVERED)}.</li>
 * </ol>
 *
 * <p>NO_DEBT takes precedence over OPERATING_LOSS when both conditions hold.
 */
@Embeddable
public record InterestCoverageRatio(
        BigDecimal value,
        @Enumerated(EnumType.STRING) InterestCoverageStatus status
) implements FinancialMetric {

    public static CalculationResult<InterestCoverageRatio> compute(FinancialDataSnapshot snapshot) {
        BigDecimal ebit = snapshot.ebit();
        BigDecimal interest = snapshot.interestExpense();

        // 1. EBIT null — only true error: cannot determine any status
        if (ebit == null) {
            return CalculationResult.missingData("ebit");
        }

        // 2. No interest expense — nothing to cover; positive NO_DEBT signal (precedence over OPERATING_LOSS)
        if (interest == null || interest.compareTo(BigDecimal.ZERO) == 0) {
            return CalculationResult.success(new InterestCoverageRatio(null, InterestCoverageStatus.NO_DEBT));
        }

        // 3. Operating loss — ratio would be numerically misleading
        if (ebit.compareTo(BigDecimal.ZERO) < 0) {
            return CalculationResult.success(new InterestCoverageRatio(null, InterestCoverageStatus.OPERATING_LOSS));
        }

        // 4. Normal case — compute the ratio
        BigDecimal ratio = FinancialMetric.divide(ebit, interest);
        return CalculationResult.success(new InterestCoverageRatio(ratio, InterestCoverageStatus.COVERED));
    }
}
