package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("QuickRatio Value Object Tests")
class QuickRatioTest {

    @Test
    @DisplayName("Valid assets and liabilities should compute QuickRatio correctly")
    void testValidDataComputesQuickRatio() {
        // Given: Snapshot with valid current assets, liabilities and inventory
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: Result should be successful ((500000 - 100000) / 200000 = 2.0)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("2.0000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Null inventory should be treated as zero")
    void testNullInventoryTreatedAsZero() {
        // Given: Snapshot with null inventory
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(null)
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: Result should be successful (500000 / 200000 = 2.5)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("2.5000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Zero inventory computes same as null inventory")
    void testZeroInventoryComputesSameAsNull() {
        // Given: Two snapshots - one with zero inventory, one with null
        var snapshotZero = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(BigDecimal.ZERO)
                .build();

        var snapshotNull = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(null)
                .build();

        // When: Computing QuickRatio for both
        CalculationResult<QuickRatio> resultZero = QuickRatio.compute(snapshotZero);
        CalculationResult<QuickRatio> resultNull = QuickRatio.compute(snapshotNull);

        // Then: Results should be equal
        assertThat(resultZero).isInstanceOf(CalculationResult.Success.class);
        assertThat(resultNull).isInstanceOf(CalculationResult.Success.class);

        BigDecimal[] ratioZero = new BigDecimal[1];
        BigDecimal[] ratioNull = new BigDecimal[1];

        resultZero.onSuccess(r -> ratioZero[0] = r.value());
        resultNull.onSuccess(r -> ratioNull[0] = r.value());

        assertThat(ratioZero[0]).isEqualTo(ratioNull[0]);
    }

    @Test
    @DisplayName("Different inventory values produce different QuickRatios")
    void testDifferentInventoriesProduceDifferentRatios() {
        // Given: Two snapshots with different inventory values
        var snapshot1 = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("100000"))
                .build();

        var snapshot2 = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("300000"))
                .build();

        // When: Computing QuickRatio for both
        CalculationResult<QuickRatio> result1 = QuickRatio.compute(snapshot1);
        CalculationResult<QuickRatio> result2 = QuickRatio.compute(snapshot2);

        // Then: Ratios should be different
        assertThat(result1).isInstanceOf(CalculationResult.Success.class);
        assertThat(result2).isInstanceOf(CalculationResult.Success.class);

        BigDecimal[] ratio1 = new BigDecimal[1];
        BigDecimal[] ratio2 = new BigDecimal[1];

        result1.onSuccess(r -> ratio1[0] = r.value());
        result2.onSuccess(r -> ratio2[0] = r.value());

        assertThat(ratio1[0]).isCloseTo(new BigDecimal("2.0000"), within(new BigDecimal("0.0001")));
        assertThat(ratio2[0]).isCloseTo(new BigDecimal("1.0000"), within(new BigDecimal("0.0001")));
    }

    @Test
    @DisplayName("Null totalCurrentAssets should fail with MISSING_DATA")
    void testNullTotalCurrentAssetsShouldFail() {
        // Given: Snapshot with null totalCurrentAssets
        var snapshot = baseSnapshot()
                .totalCurrentAssets(null)
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("totalCurrentAssets");
        });
    }

    @Test
    @DisplayName("Null totalCurrentLiabilities should fail with MISSING_DATA")
    void testNullTotalCurrentLiabilitiesShouldFail() {
        // Given: Snapshot with null totalCurrentLiabilities
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(null)
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: Should fail with MISSING_DATA
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
            assertThat(failure.reason()).contains("totalCurrentLiabilities");
        });
    }

    @Test
    @DisplayName("Zero totalCurrentLiabilities should fail with DIVISION_BY_ZERO")
    void testZeroTotalCurrentLiabilitiesShouldFail() {
        // Given: Snapshot with zero totalCurrentLiabilities
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(BigDecimal.ZERO)
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: Should fail with DIVISION_BY_ZERO
        assertThat(result).isInstanceOf(CalculationResult.Failure.class);

        result.onFailure(failure -> {
            assertThat(failure.type()).isEqualTo(CalculationErrorType.DIVISION_BY_ZERO);
            assertThat(failure.reason()).contains("totalCurrentLiabilities");
        });
    }

    @Test
    @DisplayName("Result should have exactly 4 decimal places (SCALE = 4)")
    void testResultPrecision() {
        // Given: Complete snapshot with values producing non-round result
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("333333"))
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: Result should have scale of 4
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value().scale()).isEqualTo(4));
    }

    @Test
    @DisplayName("High QuickRatio indicates strong liquidity")
    void testHighQuickRatioIndicatesStrongLiquidity() {
        // Given: Current assets much higher than liabilities
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("1000000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: QuickRatio should be high (4.5)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("4.5000"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Low QuickRatio indicates weak liquidity")
    void testLowQuickRatioIndicatesWeakLiquidity() {
        // Given: Quick assets barely cover liabilities
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("250000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: QuickRatio should be low (0.75 - below 1.0 threshold)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("0.7500"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("Inventory larger than current assets produces negative numerator")
    void testLargeInventoryProducesNegativeQuickAssets() {
        // Given: Inventory larger than total current assets (rare but possible edge case)
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("100000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("150000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: Should compute negative QuickRatio (-50000 / 200000 = -0.25)
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("-0.2500"), within(new BigDecimal("0.0001"))));
    }

    @Test
    @DisplayName("QuickRatio equals 1.0 when quick assets equal liabilities")
    void testQuickRatioEqualsOneWhenAssetsEqualLiabilities() {
        // Given: Quick assets equal current liabilities
        var snapshot = baseSnapshot()
                .totalCurrentAssets(new BigDecimal("300000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .inventory(new BigDecimal("100000"))
                .build();

        // When: Computing QuickRatio
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        // Then: QuickRatio should be exactly 1.0
        assertThat(result).isInstanceOf(CalculationResult.Success.class);

        result.onSuccess(ratio -> assertThat(ratio.value())
                .isCloseTo(new BigDecimal("1.0000"), within(new BigDecimal("0.0001"))));
    }

    private static FinancialDataSnapshot.FinancialDataSnapshotBuilder baseSnapshot() {
        return FinancialDataSnapshot.builder()
                .totalAssets(new BigDecimal("1000000"))
                .totalLiabilities(new BigDecimal("400000"))
                .retainedEarnings(new BigDecimal("150000"))
                .ebit(new BigDecimal("100000"))
                .interestExpense(new BigDecimal("50000"))
                .totalShareholderEquity(new BigDecimal("600000"))
                .totalRevenue(new BigDecimal("800000"));
    }
}

