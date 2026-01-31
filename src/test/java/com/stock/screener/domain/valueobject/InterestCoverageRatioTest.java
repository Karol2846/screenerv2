package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("InterestCoverageRatio Value Object Tests")
class InterestCoverageRatioTest {

    @Test
    @DisplayName("Valid EBIT and interestExpense should compute ratio correctly")
    void testValidDataComputesInterestCoverageRatio() {
        // Given: Snapshot with valid EBIT and interest expense
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("1000000"))
                .interestExpense(new BigDecimal("100000"))
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Result should be successful (1000000 / 100000 = 10.0)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("10.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Different EBIT values produce different coverage ratios")
    void testDifferentEbitProducesDifferentRatios() {
        // Given: Two snapshots with different EBIT values
        var snapshot1 = baseSnapshot()
                .ebit(new BigDecimal("500000"))
                .interestExpense(new BigDecimal("100000"))
                .build();

        var snapshot2 = baseSnapshot()
                .ebit(new BigDecimal("2000000"))
                .interestExpense(new BigDecimal("100000"))
                .build();

        // When: Computing ratios for both
        CalculationResult<InterestCoverageRatio> result1 = InterestCoverageRatio.compute(snapshot1);
        CalculationResult<InterestCoverageRatio> result2 = InterestCoverageRatio.compute(snapshot2);

        // Then: Ratios should be different
        assertThat(result1).isInstanceOf(CalculationResult.Success.class);
        assertThat(result2).isInstanceOf(CalculationResult.Success.class);

        BigDecimal[] ratio1 = new BigDecimal[1];
        BigDecimal[] ratio2 = new BigDecimal[1];

        result1.onSuccess(r -> ratio1[0] = r.value());
        result2.onSuccess(r -> ratio2[0] = r.value());

        assertThat(ratio1[0]).isCloseTo(new BigDecimal("5.0000"), within(new BigDecimal("0.0001")));
        assertThat(ratio2[0]).isCloseTo(new BigDecimal("20.0000"), within(new BigDecimal("0.0001")));
    }

    @Test
    @DisplayName("Null EBIT should fail with MISSING_DATA")
    void testNullEbitShouldFail() {
        // Given: Snapshot with null EBIT
        var snapshot = baseSnapshot()
                .ebit(null)
                .interestExpense(new BigDecimal("100000"))
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("ebit");
        });
    }

    @Test
    @DisplayName("Null interestExpense should fail with MISSING_DATA")
    void testNullInterestExpenseShouldFail() {
        // Given: Snapshot with null interestExpense
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("1000000"))
                .interestExpense(null)
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("interestExpense");
        });
    }

    @Test
    @DisplayName("Zero interestExpense should fail with DIVISION_BY_ZERO")
    void testZeroInterestExpenseShouldFail() {
        // Given: Snapshot with zero interestExpense
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("1000000"))
                .interestExpense(BigDecimal.ZERO)
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Should fail with DIVISION_BY_ZERO
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.DIVISION_BY_ZERO);
            assertThat(failure.reason()).contains("interestExpense");
        });
    }

    @Test
    @DisplayName("Result should have exactly 4 decimal places (SCALE = 4)")
    void testResultPrecision() {
        // Given: Complete snapshot with values producing non-round result
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("1000000"))
                .interestExpense(new BigDecimal("333333"))
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Result should have scale of 4
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value().scale()).isEqualTo(4));
    }

    @Test
    @DisplayName("High coverage ratio indicates strong debt servicing ability")
    void testHighCoverageRatio() {
        // Given: EBIT significantly higher than interest expense
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("10000000"))
                .interestExpense(new BigDecimal("100000"))
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Ratio should be very high (100x)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("100.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Low coverage ratio indicates weak debt servicing ability")
    void testLowCoverageRatio() {
        // Given: EBIT barely covers interest expense
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("150000"))
                .interestExpense(new BigDecimal("100000"))
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Ratio should be low (1.5x - dangerous territory)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("1.5000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Negative EBIT produces negative coverage ratio")
    void testNegativeEbitProducesNegativeRatio() {
        // Given: Negative EBIT (operating loss)
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("-500000"))
                .interestExpense(new BigDecimal("100000"))
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Ratio should be negative (company cannot cover interest)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("-5.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("EBIT less than interest expense produces ratio below 1")
    void testEbitLessThanInterestProducesSubOneRatio() {
        // Given: EBIT less than interest expense
        var snapshot = baseSnapshot()
                .ebit(new BigDecimal("50000"))
                .interestExpense(new BigDecimal("100000"))
                .build();

        // When: Computing InterestCoverageRatio
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        // Then: Ratio should be below 1 (0.5)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("0.5000"), within(new BigDecimal("0.0001"))));
    }

    private static FinancialDataSnapshot.FinancialDataSnapshotBuilder baseSnapshot() {
        return FinancialDataSnapshot.builder()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .totalAssets(new BigDecimal("1000000"))
                .totalLiabilities(new BigDecimal("400000"))
                .retainedEarnings(new BigDecimal("150000"))
                .totalShareholderEquity(new BigDecimal("600000"))
                .inventory(new BigDecimal("100000"))
                .totalRevenue(new BigDecimal("800000"));
    }
}

