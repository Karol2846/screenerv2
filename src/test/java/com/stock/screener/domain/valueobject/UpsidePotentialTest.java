package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.CalculationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.stock.screener.domain.valueobject.fixtures.MarketDataSnapshotFixture.aMarketDataSnapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("UpsidePotential Value Object Tests")
class UpsidePotentialTest {

    @Test
    @DisplayName("Valid targetPrice and currentPrice should compute UpsidePotential correctly")
    void testValidDataComputesUpsidePotential() {
        // Given: Snapshot with target price higher than current price
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withTargetPrice("125.00")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Result should be successful with expected percentage ((125 - 100) / 100 * 100 = 25%)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(upside -> assertThat(upside.value())
                .isCloseTo(new BigDecimal("25.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Negative upside when current price exceeds target price")
    void testNegativeUpsidePotential() {
        // Given: Snapshot with target price lower than current price
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("200.00")
                .withTargetPrice("150.00")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Result should be negative ((150 - 200) / 200 * 100 = -25%)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(upside -> assertThat(upside.value())
                .isCloseTo(new BigDecimal("-25.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Different price gaps produce different upside percentages")
    void testDifferentPriceGapsProduceDifferentUpsides() {
        // Given: Two snapshots with different price gaps
        var snapshot1 = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withTargetPrice("110.00")
                .build();

        var snapshot2 = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withTargetPrice("150.00")
                .build();

        // When: Computing UpsidePotential for both
        CalculationResult<UpsidePotential> result1 = UpsidePotential.compute(snapshot1);
        CalculationResult<UpsidePotential> result2 = UpsidePotential.compute(snapshot2);

        // Then: Percentages should be different
        assertThat(result1).isInstanceOf(CalculationResult.Success.class);
        assertThat(result2).isInstanceOf(CalculationResult.Success.class);

        BigDecimal[] upside1 = new BigDecimal[1];
        BigDecimal[] upside2 = new BigDecimal[1];

        result1.onSuccess(u -> upside1[0] = u.value());
        result2.onSuccess(u -> upside2[0] = u.value());

        assertThat(upside1[0]).isCloseTo(new BigDecimal("10.0000"), within(new BigDecimal("0.0001")));
        assertThat(upside2[0]).isCloseTo(new BigDecimal("50.0000"), within(new BigDecimal("0.0001")));
    }

    @Test
    @DisplayName("Null targetPrice should fail with MISSING_DATA")
    void testNullTargetPriceShouldFail() {
        // Given: Snapshot with null targetPrice
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withNullTargetPrice()
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("targetPrice");
        });
    }

    @Test
    @DisplayName("Null currentPrice should fail with MISSING_DATA")
    void testNullCurrentPriceShouldFail() {
        // Given: Snapshot with null currentPrice
        var snapshot = aMarketDataSnapshot()
                .withNullCurrentPrice()
                .withTargetPrice("125.00")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("currentPrice");
        });
    }

    @Test
    @DisplayName("Zero currentPrice should fail with DIVISION_BY_ZERO")
    void testZeroCurrentPriceShouldFail() {
        // Given: Snapshot with zero currentPrice
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("0")
                .withTargetPrice("125.00")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Should fail with DIVISION_BY_ZERO
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.DIVISION_BY_ZERO);
            assertThat(failure.reason()).contains("currentPrice");
        });
    }

    @Test
    @DisplayName("Result should have exactly 4 decimal places (SCALE = 4)")
    void testResultPrecision() {
        // Given: Complete snapshot
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("123.45")
                .withTargetPrice("167.89")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Result should have scale of 4
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(upside -> assertThat(upside.value().scale()).isEqualTo(4));
    }

    @Test
    @DisplayName("Equal targetPrice and currentPrice produces zero upside")
    void testEqualPricesProduceZeroUpside() {
        // Given: Snapshot where target equals current price
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withTargetPrice("100.00")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Upside should be zero
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(upside -> assertThat(upside.value())
                .isCloseTo(BigDecimal.ZERO, within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Large upside potential computed correctly")
    void testLargeUpsidePotential() {
        // Given: Snapshot with large price gap (100% upside)
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("50.00")
                .withTargetPrice("100.00")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Upside should be 100%
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(upside -> assertThat(upside.value())
                .isCloseTo(new BigDecimal("100.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Very small upside potential computed correctly")
    void testSmallUpsidePotential() {
        // Given: Snapshot with very small price gap
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withTargetPrice("100.50")
                .build();

        // When: Computing UpsidePotential
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        // Then: Upside should be 0.5%
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(upside -> assertThat(upside.value())
                .isCloseTo(new BigDecimal("0.5000"), within(new BigDecimal("0.0001"))));
    }
}
