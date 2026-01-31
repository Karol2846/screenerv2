package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("ForwardPeg Value Object Tests")
class ForwardPegTest {

    @Test
    @DisplayName("Valid forwardPeRatio and forwardEpsGrowth should compute ForwardPeg correctly")
    void testValidDataComputesForwardPeg() {
        // Given: Snapshot with valid forward PE and EPS growth
        var snapshot = baseSnapshot()
                .forwardPeRatio(new BigDecimal("20.0"))
                .forwardEpsGrowth(new BigDecimal("10.0"))
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: Result should be successful with expected value (20.0 / 10.0 = 2.0)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(peg -> assertThat(peg.value())
                .isCloseTo(new BigDecimal("2.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Different PE ratios produce different ForwardPeg values")
    void testDifferentPeRatiosProduceDifferentValues() {
        // Given: Two snapshots with different PE ratios
        var snapshot1 = baseSnapshot()
                .forwardPeRatio(new BigDecimal("15.0"))
                .forwardEpsGrowth(new BigDecimal("10.0"))
                .build();

        var snapshot2 = baseSnapshot()
                .forwardPeRatio(new BigDecimal("30.0"))
                .forwardEpsGrowth(new BigDecimal("10.0"))
                .build();

        // When: Computing ForwardPeg for both
        CalculationResult<ForwardPeg> result1 = ForwardPeg.compute(snapshot1);
        CalculationResult<ForwardPeg> result2 = ForwardPeg.compute(snapshot2);

        // Then: Values should be different
        assertThat(result1).isInstanceOf(CalculationResult.Success.class);
        assertThat(result2).isInstanceOf(CalculationResult.Success.class);

        BigDecimal[] peg1 = new BigDecimal[1];
        BigDecimal[] peg2 = new BigDecimal[1];

        result1.onSuccess(p -> peg1[0] = p.value());
        result2.onSuccess(p -> peg2[0] = p.value());

        assertThat(peg1[0]).isCloseTo(new BigDecimal("1.5000"), within(new BigDecimal("0.0001")));
        assertThat(peg2[0]).isCloseTo(new BigDecimal("3.0000"), within(new BigDecimal("0.0001")));
    }

    @Test
    @DisplayName("Null forwardPeRatio should fail with MISSING_DATA")
    void testNullForwardPeRatioShouldFail() {
        // Given: Snapshot with null forwardPeRatio
        var snapshot = baseSnapshot()
                .forwardPeRatio(null)
                .forwardEpsGrowth(new BigDecimal("10.0"))
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("forwardPeRatio");
        });
    }

    @Test
    @DisplayName("Null forwardEpsGrowth should fail with MISSING_DATA")
    void testNullForwardEpsGrowthShouldFail() {
        // Given: Snapshot with null forwardEpsGrowth
        var snapshot = baseSnapshot()
                .forwardPeRatio(new BigDecimal("20.0"))
                .forwardEpsGrowth(null)
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("forwardEpsGrowth");
        });
    }

    @Test
    @DisplayName("Zero forwardEpsGrowth should fail with DIVISION_BY_ZERO")
    void testZeroForwardEpsGrowthShouldFail() {
        // Given: Snapshot with zero forwardEpsGrowth
        var snapshot = baseSnapshot()
                .forwardPeRatio(new BigDecimal("20.0"))
                .forwardEpsGrowth(BigDecimal.ZERO)
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: Should fail with DIVISION_BY_ZERO
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.DIVISION_BY_ZERO);
            assertThat(failure.reason()).contains("forwardEpsGrowth");
        });
    }

    @Test
    @DisplayName("Result should have exactly 4 decimal places (SCALE = 4)")
    void testResultPrecision() {
        // Given: Complete snapshot
        var snapshot = baseSnapshot()
                .forwardPeRatio(new BigDecimal("25.0"))
                .forwardEpsGrowth(new BigDecimal("7.0"))
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: Result should have scale of 4
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(peg -> assertThat(peg.value().scale()).isEqualTo(4));
    }

    @Test
    @DisplayName("Low PE with high growth produces low PEG (undervalued)")
    void testLowPeHighGrowthProducesLowPeg() {
        // Given: Low PE with high growth
        var snapshot = baseSnapshot()
                .forwardPeRatio(new BigDecimal("10.0"))
                .forwardEpsGrowth(new BigDecimal("25.0"))
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: PEG should be low (< 1 indicates undervalued)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(peg -> assertThat(peg.value())
                .isCloseTo(new BigDecimal("0.4000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("High PE with low growth produces high PEG (overvalued)")
    void testHighPeLowGrowthProducesHighPeg() {
        // Given: High PE with low growth
        var snapshot = baseSnapshot()
                .forwardPeRatio(new BigDecimal("50.0"))
                .forwardEpsGrowth(new BigDecimal("5.0"))
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: PEG should be high (> 2 indicates overvalued)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(peg -> assertThat(peg.value())
                .isCloseTo(new BigDecimal("10.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Negative forwardEpsGrowth should compute PEG correctly (but result is negative)")
    void testNegativeEpsGrowthComputesNegativePeg() {
        // Given: Negative EPS growth (company shrinking)
        var snapshot = baseSnapshot()
                .forwardPeRatio(new BigDecimal("20.0"))
                .forwardEpsGrowth(new BigDecimal("-5.0"))
                .build();

        // When: Computing ForwardPeg
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        // Then: Should still compute (negative PEG indicates declining earnings)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(peg -> assertThat(peg.value())
                .isCloseTo(new BigDecimal("-4.0000"), within(new BigDecimal("0.0001"))));
    }

    private static MarketDataSnapshot.MarketDataSnapshotBuilder baseSnapshot() {
        return MarketDataSnapshot.builder()
                .currentPrice(new BigDecimal("150.00"))
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(new BigDecimal("500000000"))
                .targetPrice(new BigDecimal("180.00"))
                .forwardRevenueGrowth(new BigDecimal("12.5"));
    }
}

