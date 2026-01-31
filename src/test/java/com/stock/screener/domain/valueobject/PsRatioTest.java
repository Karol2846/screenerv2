package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("PsRatio Value Object Tests")
class PsRatioTest {

    @Test
    @DisplayName("Valid marketCap and revenueTTM should compute PS ratio correctly")
    void testValidDataComputesPsRatio() {
        // Given: Snapshot with valid marketCap and revenueTTM
        var snapshot = baseSnapshot()
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(new BigDecimal("500000000"))
                .build();

        // When: Computing PS ratio
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        // Then: Result should be successful with expected ratio (1000000000 / 500000000 = 2.0)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("2.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Different market caps produce different PS ratios")
    void testDifferentMarketCapsProduceDifferentRatios() {
        // Given: Two snapshots with different market caps
        var snapshot1 = baseSnapshot()
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(new BigDecimal("500000000"))
                .build();

        var snapshot2 = baseSnapshot()
                .marketCap(new BigDecimal("2000000000"))
                .revenueTTM(new BigDecimal("500000000"))
                .build();

        // When: Computing PS ratios
        CalculationResult<PsRatio> result1 = PsRatio.compute(snapshot1);
        CalculationResult<PsRatio> result2 = PsRatio.compute(snapshot2);

        // Then: Ratios should be different
        assertThat(result1).isInstanceOf(CalculationResult.Success.class);
        assertThat(result2).isInstanceOf(CalculationResult.Success.class);

        BigDecimal[] ratio1 = new BigDecimal[1];
        BigDecimal[] ratio2 = new BigDecimal[1];

        result1.onSuccess(r -> ratio1[0] = r.value());
        result2.onSuccess(r -> ratio2[0] = r.value());

        assertThat(ratio1[0]).isNotEqualTo(ratio2[0]);
        assertThat(ratio2[0]).isCloseTo(new BigDecimal("4.0000"), within(new BigDecimal("0.0001")));
    }

    @Test
    @DisplayName("Null marketCap should fail with MISSING_DATA")
    void testNullMarketCapShouldFail() {
        // Given: Snapshot with null marketCap
        var snapshot = baseSnapshot()
                .marketCap(null)
                .revenueTTM(new BigDecimal("500000000"))
                .build();

        // When: Computing PS ratio
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("marketCap");
        });
    }

    @Test
    @DisplayName("Null revenueTTM should fail with MISSING_DATA")
    void testNullRevenueTTMShouldFail() {
        // Given: Snapshot with null revenueTTM
        var snapshot = baseSnapshot()
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(null)
                .build();

        // When: Computing PS ratio
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("revenueTTM");
        });
    }

    @Test
    @DisplayName("Zero revenueTTM should fail with DIVISION_BY_ZERO")
    void testZeroRevenueTTMShouldFail() {
        // Given: Snapshot with zero revenueTTM
        var snapshot = baseSnapshot()
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(BigDecimal.ZERO)
                .build();

        // When: Computing PS ratio
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        // Then: Should fail with DIVISION_BY_ZERO
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.DIVISION_BY_ZERO);
            assertThat(failure.reason()).contains("revenueTTM");
        });
    }

    @Test
    @DisplayName("Result should have exactly 4 decimal places (SCALE = 4)")
    void testResultPrecision() {
        // Given: Complete snapshot
        var snapshot = baseSnapshot()
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(new BigDecimal("333333333"))
                .build();

        // When: Computing PS ratio
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        // Then: Result should have scale of 4
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value().scale()).isEqualTo(4));
    }

    @Test
    @DisplayName("Low revenue relative to marketCap produces high PS ratio")
    void testLowRevenueProducesHighRatio() {
        // Given: Snapshot with low revenue relative to market cap
        var snapshot = baseSnapshot()
                .marketCap(new BigDecimal("10000000000"))
                .revenueTTM(new BigDecimal("100000000"))
                .build();

        // When: Computing PS ratio
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        // Then: PS ratio should be high (100)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("100.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("High revenue relative to marketCap produces low PS ratio")
    void testHighRevenueProducesLowRatio() {
        // Given: Snapshot with high revenue relative to market cap
        var snapshot = baseSnapshot()
                .marketCap(new BigDecimal("100000000"))
                .revenueTTM(new BigDecimal("1000000000"))
                .build();

        // When: Computing PS ratio
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        // Then: PS ratio should be low (0.1)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("0.1000"), within(new BigDecimal("0.0001"))));
    }

    private static MarketDataSnapshot.MarketDataSnapshotBuilder baseSnapshot() {
        return MarketDataSnapshot.builder()
                .currentPrice(new BigDecimal("150.00"))
                .forwardPeRatio(new BigDecimal("25.0"))
                .targetPrice(new BigDecimal("180.00"))
                .forwardEpsGrowth(new BigDecimal("15.0"))
                .forwardRevenueGrowth(new BigDecimal("12.5"));
    }
}

