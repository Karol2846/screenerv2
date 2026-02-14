package com.stock.screener.domain.entity;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.MetricType;
import com.stock.screener.domain.valueobject.ReportIntegrityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.stock.screener.domain.valueobject.fixtures.MarketDataSnapshotFixture.aMarketDataSnapshot;
import static com.stock.screener.domain.valueobject.fixtures.MarketDataSnapshotFixture.avOnlySnapshot;
import static com.stock.screener.domain.valueobject.fixtures.MarketDataSnapshotFixture.yhOnlySnapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("MonthlyReport Entity Tests - Sociable Testing")
class MonthlyReportTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0001");

    private MonthlyReport monthlyReport;

    @BeforeEach
    void setUp() {
        monthlyReport = new MonthlyReport();
    }

    @Test
    @DisplayName("updateMetrics() with complete snapshot updates all simple fields atomically")
    void testUpdateMetricsUpdatesSimpleFields() {
        // Given: Complete market data snapshot
        var snapshot = aMarketDataSnapshot().build();

        // When: Updating metrics atomically
        monthlyReport.updateMetrics(snapshot);

        // Then: All simple fields should be updated
        assertThat(monthlyReport.forwardPeRatio)
                .isEqualByComparingTo(new BigDecimal("25.0"));

        assertThat(monthlyReport.forwardEpsGrowth)
                .isEqualByComparingTo(new BigDecimal("15.0"));

        assertThat(monthlyReport.forwardRevenueGrowth)
                .isEqualByComparingTo(new BigDecimal("12.5"));

        assertThat(monthlyReport.targetPrice)
                .isEqualByComparingTo(new BigDecimal("180.00"));

        assertThat(monthlyReport.analystRatings)
                .isNotNull()
                .satisfies(ratings -> {
                    assertThat(ratings.strongBuy()).isEqualTo(5);
                    assertThat(ratings.buy()).isEqualTo(10);
                    assertThat(ratings.hold()).isEqualTo(3);
                    assertThat(ratings.sell()).isEqualTo(1);
                    assertThat(ratings.strongSell()).isEqualTo(0);
                });
    }

    @Test
    @DisplayName("updateMetrics() correctly computes PsRatio Value Object")
    void testUpdateMetricsComputesPsRatio() {
        // Given: Snapshot with valid market cap and revenue
        var snapshot = aMarketDataSnapshot()
                .withMarketCap("1000000000")
                .withRevenueTTM("500000000")
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: PsRatio should be computed correctly (1000000000 / 500000000 = 2.0)
        assertThat(monthlyReport.psRatio)
                .isNotNull()
                .satisfies(psRatio -> assertThat(psRatio.value())
                        .isCloseTo(new BigDecimal("2.0000"), within(PRECISION)));

        // And: No calculation errors for PS_RATIO
        assertNoCalculationErrorFor(MetricType.PS_RATIO);
    }

    @Test
    @DisplayName("updateMetrics() correctly computes ForwardPeg Value Object")
    void testUpdateMetricsComputesForwardPeg() {
        // Given: Snapshot with valid forward PE and EPS growth
        var snapshot = aMarketDataSnapshot()
                .withForwardPeRatio("20.0")
                .withForwardEpsGrowth("10.0")
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should be computed correctly (20.0 / 10.0 = 2.0)
        assertThat(monthlyReport.forwardPegRatio)
                .isNotNull()
                .satisfies(peg -> assertThat(peg.value())
                        .isCloseTo(new BigDecimal("2.0000"), within(PRECISION)));

        // And: No calculation errors for FORWARD_PEG
        assertNoCalculationErrorFor(MetricType.FORWARD_PEG);
    }

    @Test
    @DisplayName("updateMetrics() correctly computes UpsidePotential Value Object")
    void testUpdateMetricsComputesUpsidePotential() {
        // Given: Snapshot with target price and current price
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withTargetPrice("125.00")
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Upside should be computed correctly ((125 - 100) / 100 * 100 = 25%)
        assertThat(monthlyReport.upsidePotential)
                .isNotNull()
                .satisfies(upside -> assertThat(upside.value())
                        .isCloseTo(new BigDecimal("25.0000"), within(PRECISION)));

        // And: No calculation errors for UPSIDE_POTENTIAL
        assertNoCalculationErrorFor(MetricType.UPSIDE_POTENTIAL);
    }

    @Test
    @DisplayName("updateMetrics() updates all complex metrics in one atomic operation")
    void testUpdateMetricsIsAtomic() {
        // Given: Complete snapshot
        var snapshot = aMarketDataSnapshot().build();

        // When: Updating metrics once
        monthlyReport.updateMetrics(snapshot);

        // Then: All three complex metrics should be populated
        assertThat(monthlyReport.psRatio).isNotNull();
        assertThat(monthlyReport.forwardPegRatio).isNotNull();
        assertThat(monthlyReport.upsidePotential).isNotNull();

        // And: No calculation errors
        assertThat(monthlyReport.calculationErrors).isEmpty();

        // And: Integrity status should be COMPLETE
        assertThat(monthlyReport.integrityStatus).isEqualTo(ReportIntegrityStatus.COMPLETE);
    }

    // === Calculation Error Tracking Tests ===

    @Test
    @DisplayName("Missing marketCap causes PsRatio to fail and track error")
    void testMissingMarketCapTracksError() {
        // Given: Snapshot with missing marketCap
        var snapshot = aMarketDataSnapshot()
                .withNullMarketCap()
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: PsRatio should be null
        assertThat(monthlyReport.psRatio).isNull();

        // And: Calculation error should be tracked with correct field name
        assertMissingDataError(MetricType.PS_RATIO, "marketCap");
    }

    @Test
    @DisplayName("Zero revenueTTM causes PsRatio to fail with DIVISION_BY_ZERO")
    void testZeroRevenueCausesPsRatioFailure() {
        // Given: Snapshot with zero revenue
        var snapshot = aMarketDataSnapshot()
                .withZeroRevenueTTM()
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: PsRatio should be null
        assertThat(monthlyReport.psRatio).isNull();

        // And: Error should be tracked as DIVISION_BY_ZERO
        assertCalculationError(MetricType.PS_RATIO);
    }

    @Test
    @DisplayName("Missing forwardPeRatio causes ForwardPeg to fail and track error")
    void testMissingForwardPeTracksError() {
        // Given: Snapshot with missing forwardPeRatio
        var snapshot = aMarketDataSnapshot()
                .withNullForwardPeRatio()
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should be null
        assertThat(monthlyReport.forwardPegRatio).isNull();

        // And: Calculation error should be tracked
        assertMissingDataError(MetricType.FORWARD_PEG, "forwardPeRatio");
    }

    @Test
    @DisplayName("Zero forwardEpsGrowth causes ForwardPeg to fail with DIVISION_BY_ZERO")
    void testZeroEpsGrowthCausesForwardPegFailure() {
        // Given: Snapshot with zero EPS growth
        var snapshot = aMarketDataSnapshot()
                .withZeroForwardEpsGrowth()
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should be null
        assertThat(monthlyReport.forwardPegRatio).isNull();

        // And: Error should be tracked as DIVISION_BY_ZERO
        assertCalculationError(MetricType.FORWARD_PEG);
    }

    @Test
    @DisplayName("Missing targetPrice causes UpsidePotential to fail and track error")
    void testMissingTargetPriceTracksError() {
        // Given: Snapshot with missing targetPrice
        var snapshot = aMarketDataSnapshot()
                .withNullTargetPrice()
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: UpsidePotential should be null
        assertThat(monthlyReport.upsidePotential).isNull();

        // And: Calculation error should be tracked
        assertMissingDataError(MetricType.UPSIDE_POTENTIAL, "targetPrice");
    }

    @Test
    @DisplayName("Multiple missing fields cause multiple errors to be tracked")
    void testMultipleMissingFieldsTrackMultipleErrors() {
        // Given: Snapshot with multiple missing fields
        var snapshot = aMarketDataSnapshot()
                .withNullMarketCap()          // PsRatio will fail
                .withNullForwardPeRatio()     // ForwardPeg will fail
                .withNullTargetPrice()        // UpsidePotential will fail
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: All three metrics should be null
        assertThat(monthlyReport.psRatio).isNull();
        assertThat(monthlyReport.forwardPegRatio).isNull();
        assertThat(monthlyReport.upsidePotential).isNull();

        // And: Three errors should be tracked
        assertThat(monthlyReport.calculationErrors).hasSize(3);

        assertThat(monthlyReport.calculationErrors)
                .anyMatch(error -> error.metric() == MetricType.PS_RATIO);

        assertThat(monthlyReport.calculationErrors)
                .anyMatch(error -> error.metric() == MetricType.FORWARD_PEG);

        assertThat(monthlyReport.calculationErrors)
                .anyMatch(error -> error.metric() == MetricType.UPSIDE_POTENTIAL);
    }

    @Test
    @DisplayName("Errors from previous update are cleared on subsequent update")
    void testErrorsClearedOnSubsequentUpdate() {
        // Given: First update with missing data
        var incompleteSnapshot = aMarketDataSnapshot()
                .withNullMarketCap()
                .build();

        monthlyReport.updateMetrics(incompleteSnapshot);
        assertThat(monthlyReport.calculationErrors).isNotEmpty();

        // When: Second update with complete data
        var completeSnapshotData = aMarketDataSnapshot().build();
        monthlyReport.updateMetrics(completeSnapshotData);

        // Then: Previous errors should be cleared
        assertThat(monthlyReport.calculationErrors).isEmpty();

        // And: All metrics should be computed
        assertThat(monthlyReport.psRatio).isNotNull();
        assertThat(monthlyReport.forwardPegRatio).isNotNull();
        assertThat(monthlyReport.upsidePotential).isNotNull();
    }

    // === ReportIntegrityStatus Tests ===

    @Test
    @DisplayName("Complete snapshot results in COMPLETE integrity status")
    void testCompleteSnapshotProducesCompleteStatus() {
        // Given: Complete snapshot with all AV and YH data
        var snapshot = aMarketDataSnapshot().build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Status should be COMPLETE
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.COMPLETE);
    }

    @Test
    @DisplayName("AV + partial YH data (only forwardEpsGrowth) allows ForwardPeg computation, status is AV_FETCHED_COMPLETED")
    void testAvWithPartialYhProducesAvFetchedCompleted() {
        // Given: Snapshot with AV data + only forwardEpsGrowth from YH
        // (missing analystRatings and forwardRevenueGrowth)
        var snapshot = aMarketDataSnapshot()
                .withNullAnalystRatings()
                .withNullForwardRevenueGrowth()
                // forwardEpsGrowth remains - allows ForwardPeg computation
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: All computed metrics should be present (including hybrid ForwardPeg)
        assertThat(monthlyReport.psRatio).isNotNull();
        assertThat(monthlyReport.forwardPegRatio).isNotNull();
        assertThat(monthlyReport.upsidePotential).isNotNull();

        // And: Partial YH data
        assertThat(monthlyReport.forwardEpsGrowth).isNotNull();
        assertThat(monthlyReport.analystRatings).isNull();
        assertThat(monthlyReport.forwardRevenueGrowth).isNull();

        // And: Status should be AV_FETCHED_COMPLETED because:
        // - AV is complete (psRatio, upsidePotential)
        // - YH is NOT complete (missing analystRatings, forwardRevenueGrowth)
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.AV_FETCHED_COMPLETED);
    }

    @Test
    @DisplayName("Only AV data (without YH) results in AV_FETCHED_COMPLETED status")
    void testAvOnlyProducesAvFetchedCompletedStatus() {
        // Given: Snapshot with only AV data (YH data missing)
        // ForwardPeg cannot be computed without forwardEpsGrowth (YH field) - that's expected
        var snapshot = avOnlySnapshot().build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Pure AV metrics should be computed
        assertThat(monthlyReport.psRatio).isNotNull();
        assertThat(monthlyReport.upsidePotential).isNotNull();
        
        // And: ForwardPeg (hybrid metric) should be null - it needs YH forwardEpsGrowth
        assertThat(monthlyReport.forwardPegRatio).isNull();

        // And: YH simple fields should be null
        assertThat(monthlyReport.forwardEpsGrowth).isNull();
        assertThat(monthlyReport.forwardRevenueGrowth).isNull();
        assertThat(monthlyReport.analystRatings).isNull();

        // And: Status should be AV_FETCHED_COMPLETED (pure AV metrics are OK)
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.AV_FETCHED_COMPLETED);
    }

    @Test
    @DisplayName("Only YH data results in YH_FETCHED_COMPLETED status")
    void testYhOnlyProducesYhCompletedStatus() {
        // Given: Snapshot with only YH data (AV data missing)
        var snapshot = yhOnlySnapshot().build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: YH simple fields should be populated
        assertThat(monthlyReport.forwardEpsGrowth).isNotNull();
        assertThat(monthlyReport.forwardRevenueGrowth).isNotNull();
        assertThat(monthlyReport.analystRatings).isNotNull();

        // And: AV-dependent metrics should be null
        assertThat(monthlyReport.psRatio).isNull();
        assertThat(monthlyReport.forwardPegRatio).isNull();
        assertThat(monthlyReport.upsidePotential).isNull();

        // And: Status should be YH_FETCHED_COMPLETED
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.YH_FETCHED_COMPLETED);
    }

    @Test
    @DisplayName("Missing both AV and YH data results in MISSING_DATA status")
    void testMissingBothProducesMissingDataStatus() {
        // Given: Snapshot with incomplete data from both sources
        var snapshot = aMarketDataSnapshot()
                // Incomplete AV data (psRatio cannot be computed)
                .withNullMarketCap()  // Missing for psRatio
                // Incomplete YH data
                .withNullForwardEpsGrowth()  // Missing
                .withNullAnalystRatings()  // Missing
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Status should be MISSING_DATA
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.MISSING_DATA);
    }

    @Test
    @DisplayName("Partial AV data (missing one field) is not considered complete")
    void testPartialAvDataNotComplete() {
        // Given: Snapshot with most AV data but missing one critical field
        var snapshot = aMarketDataSnapshot()
                .withNullRevenueTTM()  // Missing - breaks psRatio
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: psRatio should be null
        assertThat(monthlyReport.psRatio).isNull();

        // And: Even though other AV metrics might succeed, AV is not complete
        // Status depends on YH being complete
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.YH_FETCHED_COMPLETED);
    }

    @Test
    @DisplayName("Updating metrics twice with different data changes all fields")
    void testUpdateMetricsTwiceChangesAllFields() {
        // Given: First snapshot
        var snapshot1 = aMarketDataSnapshot()
                .withCurrentPrice("100.00")
                .withMarketCap("1000000000")
                .build();

        monthlyReport.updateMetrics(snapshot1);

        BigDecimal firstPrice = monthlyReport.forwardPeRatio;
        BigDecimal firstPsRatio = monthlyReport.psRatio.value();

        // When: Second update with different data
        var snapshot2 = aMarketDataSnapshot()
                .withCurrentPrice("200.00")
                .withMarketCap("2000000000")
                .withForwardPeRatio("30.0")
                .build();

        monthlyReport.updateMetrics(snapshot2);

        // Then: Simple fields should change
        assertThat(monthlyReport.forwardPeRatio)
                .isNotEqualTo(firstPrice);

        // And: Complex metrics should be recalculated
        assertThat(monthlyReport.psRatio.value())
                .isNotEqualTo(firstPsRatio);
    }

    @Test
    @DisplayName("ReportError includes timestamp and all required fields")
    void testReportErrorStructure() {
        // Given: Snapshot with missing data
        var snapshot = aMarketDataSnapshot()
                .withNullMarketCap()
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Error should have all required fields
        assertThat(monthlyReport.calculationErrors)
                .hasSize(1)
                .first()
                .satisfies(error -> {
                    assertThat(error.metric()).isEqualTo(MetricType.PS_RATIO);
                    assertThat(error.errorType()).isEqualTo(CalculationErrorType.MISSING_DATA);
                    assertThat(error.reason()).isNotBlank();
                    assertThat(error.occurredAt()).isNotNull();
                });
    }

    @Test
    @DisplayName("Negative upside potential is computed correctly when current price > target price")
    void testNegativeUpsidePotential() {
        // Given: Snapshot where current price exceeds target price
        var snapshot = aMarketDataSnapshot()
                .withCurrentPrice("200.00")
                .withTargetPrice("150.00")
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Upside should be negative ((150 - 200) / 200 * 100 = -25%)
        assertThat(monthlyReport.upsidePotential)
                .isNotNull()
                .satisfies(upside -> assertThat(upside.value())
                        .isCloseTo(new BigDecimal("-25.0000"), within(PRECISION)));
    }

    @Test
    @DisplayName("Zero currentPrice causes UpsidePotential to fail with DIVISION_BY_ZERO")
    void testZeroCurrentPriceCausesUpsidePotentialFailure() {
        // Given: Snapshot with zero current price
        var snapshot = aMarketDataSnapshot()
                .withZeroCurrentPrice()
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: UpsidePotential should be null
        assertThat(monthlyReport.upsidePotential).isNull();

        // And: Error should be tracked as DIVISION_BY_ZERO
        assertCalculationError(MetricType.UPSIDE_POTENTIAL);
    }

    // === Edge Cases & Defensive Tests ===

    @Test
    @DisplayName("updateMetrics() with null snapshot throws NullPointerException")
    void testNullSnapshotThrowsException() {
        // Given: null snapshot

        // When/Then: Should throw NPE (or consider adding null check in domain)
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> monthlyReport.updateMetrics(null)
        );
    }

    @Test
    @DisplayName("Negative forwardEpsGrowth produces valid ForwardPeg (growth stock losing momentum)")
    void testNegativeForwardEpsGrowthProducesValidPeg() {
        // Given: Negative EPS growth (company losing momentum)
        var snapshot = aMarketDataSnapshot()
                .withForwardPeRatio("20.0")
                .withForwardEpsGrowth("-5.0")
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should be computed (20 / -5 = -4.0)
        // Negative PEG indicates declining earnings - this is valid business scenario
        assertThat(monthlyReport.forwardPegRatio)
                .isNotNull()
                .satisfies(peg -> assertThat(peg.value())
                        .isCloseTo(new BigDecimal("-4.0000"), within(PRECISION)));
    }

    @Test
    @DisplayName("Very large numbers don't cause overflow in calculations")
    void testLargeNumbersHandledCorrectly() {
        // Given: Very large market cap (Apple-like)
        var snapshot = aMarketDataSnapshot()
                .withMarketCap("3000000000000")  // 3 trillion
                .withRevenueTTM("400000000000")   // 400 billion
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: PsRatio should be computed correctly (3T / 400B = 7.5)
        assertThat(monthlyReport.psRatio)
                .isNotNull()
                .satisfies(psRatio -> assertThat(psRatio.value())
                        .isCloseTo(new BigDecimal("7.5000"), within(PRECISION)));
    }

    @Test
    @DisplayName("Very small decimal values preserve precision")
    void testSmallDecimalPrecision() {
        // Given: Small fractional values
        var snapshot = aMarketDataSnapshot()
                .withForwardPeRatio("0.5")
                .withForwardEpsGrowth("0.25")
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should preserve precision (0.5 / 0.25 = 2.0)
        assertThat(monthlyReport.forwardPegRatio)
                .isNotNull()
                .satisfies(peg -> assertThat(peg.value())
                        .isCloseTo(new BigDecimal("2.0000"), within(PRECISION)));
    }

    private void assertCalculationError(MetricType metric) {
        assertThat(monthlyReport.calculationErrors)
                .as("Expected calculation error for %s with type %s", metric, CalculationErrorType.DIVISION_BY_ZERO)
                .anyMatch(error ->
                        error.metric() == metric &&
                                error.errorType() == CalculationErrorType.DIVISION_BY_ZERO
                );
    }

    private void assertNoCalculationErrorFor(MetricType metric) {
        assertThat(monthlyReport.calculationErrors)
                .as("Expected no calculation error for %s", metric)
                .noneMatch(error -> error.metric() == metric);
    }

    private void assertMissingDataError(MetricType metric, String expectedField) {
        assertThat(monthlyReport.calculationErrors)
                .as("Expected MISSING_DATA error for %s mentioning '%s'", metric, expectedField)
                .anyMatch(error ->
                        error.metric() == metric &&
                                error.errorType() == CalculationErrorType.MISSING_DATA &&
                                error.reason().contains(expectedField)
                );
    }
}
