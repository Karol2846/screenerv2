package com.stock.screener.domain.entity;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.MetricType;
import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.ReportIntegrityStatus;
import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("MonthlyReport Entity Tests - Sociable Testing")
class MonthlyReportTest {

    private MonthlyReport monthlyReport;

    @BeforeEach
    void setUp() {
        monthlyReport = new MonthlyReport();
    }

    @Test
    @DisplayName("updateMetrics() with complete snapshot updates all simple fields atomically")
    void testUpdateMetricsUpdatesSimpleFields() {
        // Given: Complete market data snapshot
        var snapshot = completeSnapshot().build();

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
        var snapshot = completeSnapshot()
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(new BigDecimal("500000000"))
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: PsRatio should be computed correctly (1000000000 / 500000000 = 2.0)
        assertThat(monthlyReport.psRatio)
                .isNotNull()
                .satisfies(psRatio -> assertThat(psRatio.value())
                        .isCloseTo(new BigDecimal("2.0000"), within(new BigDecimal("0.0001"))));

        // And: No calculation errors for PS_RATIO
        assertThat(monthlyReport.calculationErrors)
                .noneMatch(error -> error.metric() == MetricType.PS_RATIO);
    }

    @Test
    @DisplayName("updateMetrics() correctly computes ForwardPeg Value Object")
    void testUpdateMetricsComputesForwardPeg() {
        // Given: Snapshot with valid forward PE and EPS growth
        var snapshot = completeSnapshot()
                .forwardPeRatio(new BigDecimal("20.0"))
                .forwardEpsGrowth(new BigDecimal("10.0"))
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should be computed correctly (20.0 / 10.0 = 2.0)
        assertThat(monthlyReport.forwardPegRatio)
                .isNotNull()
                .satisfies(peg -> assertThat(peg.value())
                        .isCloseTo(new BigDecimal("2.0000"), within(new BigDecimal("0.0001"))));

        // And: No calculation errors for FORWARD_PEG
        assertThat(monthlyReport.calculationErrors)
                .noneMatch(error -> error.metric() == MetricType.FORWARD_PEG);
    }

    @Test
    @DisplayName("updateMetrics() correctly computes UpsidePotential Value Object")
    void testUpdateMetricsComputesUpsidePotential() {
        // Given: Snapshot with target price and current price
        var snapshot = completeSnapshot()
                .currentPrice(new BigDecimal("100.00"))
                .targetPrice(new BigDecimal("125.00"))
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Upside should be computed correctly ((125 - 100) / 100 * 100 = 25%)
        assertThat(monthlyReport.upsidePotential)
                .isNotNull()
                .satisfies(upside -> assertThat(upside.value())
                        .isCloseTo(new BigDecimal("25.0000"), within(new BigDecimal("0.0001"))));

        // And: No calculation errors for UPSIDE_POTENTIAL
        assertThat(monthlyReport.calculationErrors)
                .noneMatch(error -> error.metric() == MetricType.UPSIDE_POTENTIAL);
    }

    @Test
    @DisplayName("updateMetrics() updates all complex metrics in one atomic operation")
    void testUpdateMetricsIsAtomic() {
        // Given: Complete snapshot
        var snapshot = completeSnapshot().build();

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
        var snapshot = completeSnapshot()
                .marketCap(null)
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: PsRatio should be null
        assertThat(monthlyReport.psRatio).isNull();

        // And: Calculation error should be tracked
        assertThat(monthlyReport.calculationErrors)
                .hasSize(1)
                .anyMatch(error ->
                        error.metric() == MetricType.PS_RATIO &&
                                error.errorType() == CalculationErrorType.MISSING_DATA &&
                                error.reason().contains("marketCap")
                );
    }

    @Test
    @DisplayName("Zero revenueTTM causes PsRatio to fail with DIVISION_BY_ZERO")
    void testZeroRevenueCausesPsRatioFailure() {
        // Given: Snapshot with zero revenue
        var snapshot = completeSnapshot()
                .revenueTTM(BigDecimal.ZERO)
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: PsRatio should be null
        assertThat(monthlyReport.psRatio).isNull();

        // And: Error should be tracked as DIVISION_BY_ZERO
        assertThat(monthlyReport.calculationErrors)
                .anyMatch(error ->
                        error.metric() == MetricType.PS_RATIO &&
                                error.errorType() == CalculationErrorType.DIVISION_BY_ZERO
                );
    }

    @Test
    @DisplayName("Missing forwardPeRatio causes ForwardPeg to fail and track error")
    void testMissingForwardPeTracksError() {
        // Given: Snapshot with missing forwardPeRatio
        var snapshot = completeSnapshot()
                .forwardPeRatio(null)
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should be null
        assertThat(monthlyReport.forwardPegRatio).isNull();

        // And: Calculation error should be tracked
        assertThat(monthlyReport.calculationErrors)
                .anyMatch(error ->
                        error.metric() == MetricType.FORWARD_PEG &&
                                error.errorType() == CalculationErrorType.MISSING_DATA &&
                                error.reason().contains("forwardPeRatio")
                );
    }

    @Test
    @DisplayName("Zero forwardEpsGrowth causes ForwardPeg to fail with DIVISION_BY_ZERO")
    void testZeroEpsGrowthCausesForwardPegFailure() {
        // Given: Snapshot with zero EPS growth
        var snapshot = completeSnapshot()
                .forwardEpsGrowth(BigDecimal.ZERO)
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: ForwardPeg should be null
        assertThat(monthlyReport.forwardPegRatio).isNull();

        // And: Error should be tracked as DIVISION_BY_ZERO
        assertThat(monthlyReport.calculationErrors)
                .anyMatch(error ->
                        error.metric() == MetricType.FORWARD_PEG &&
                                error.errorType() == CalculationErrorType.DIVISION_BY_ZERO
                );
    }

    @Test
    @DisplayName("Missing targetPrice causes UpsidePotential to fail and track error")
    void testMissingTargetPriceTracksError() {
        // Given: Snapshot with missing targetPrice
        var snapshot = completeSnapshot()
                .targetPrice(null)
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: UpsidePotential should be null
        assertThat(monthlyReport.upsidePotential).isNull();

        // And: Calculation error should be tracked
        assertThat(monthlyReport.calculationErrors)
                .anyMatch(error ->
                        error.metric() == MetricType.UPSIDE_POTENTIAL &&
                                error.errorType() == CalculationErrorType.MISSING_DATA &&
                                error.reason().contains("targetPrice")
                );
    }

    @Test
    @DisplayName("Multiple missing fields cause multiple errors to be tracked")
    void testMultipleMissingFieldsTrackMultipleErrors() {
        // Given: Snapshot with multiple missing fields
        var snapshot = completeSnapshot()
                .marketCap(null)          // PsRatio will fail
                .forwardPeRatio(null)     // ForwardPeg will fail
                .targetPrice(null)        // UpsidePotential will fail
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
        var incompleteSnapshot = completeSnapshot()
                .marketCap(null)
                .build();

        monthlyReport.updateMetrics(incompleteSnapshot);
        assertThat(monthlyReport.calculationErrors).isNotEmpty();

        // When: Second update with complete data
        var completeSnapshotData = completeSnapshot().build();
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
        var snapshot = completeSnapshot().build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Status should be COMPLETE
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.COMPLETE);
    }

    @Test
    @DisplayName("Only AV data with forwardPeRatio null results in partial status")
    void testAvOnlyWithPartialDataProducesMissingStatus() {
        // Given: Snapshot with only AV data (YH data missing)
        // Note: ForwardPeg cannot be computed without forwardEpsGrowth (YH field)
        var snapshot = avOnlySnapshot().build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: AV metrics that don't depend on YH should be computed
        assertThat(monthlyReport.psRatio).isNotNull();
        assertThat(monthlyReport.upsidePotential).isNotNull();
        
        // But: ForwardPeg needs YH forwardEpsGrowth, so it will be null
        assertThat(monthlyReport.forwardPegRatio).isNull();

        // And: YH simple fields should be null
        assertThat(monthlyReport.forwardEpsGrowth).isNull();
        assertThat(monthlyReport.forwardRevenueGrowth).isNull();
        assertThat(monthlyReport.analystRatings).isNull();

        // And: Status should be MISSING_DATA because neither AV nor YH are complete
        // (AV is not complete because forwardPegRatio failed)
        assertThat(monthlyReport.integrityStatus)
                .isEqualTo(ReportIntegrityStatus.MISSING_DATA);
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
        var snapshot = MarketDataSnapshot.builder()
                // Incomplete AV data (psRatio cannot be computed)
                .currentPrice(new BigDecimal("150.00"))
                .marketCap(null)  // Missing for psRatio
                .revenueTTM(new BigDecimal("500000000"))
                .forwardPeRatio(new BigDecimal("25.0"))
                .targetPrice(new BigDecimal("180.00"))
                // Incomplete YH data
                .forwardEpsGrowth(null)  // Missing
                .forwardRevenueGrowth(new BigDecimal("12.5"))
                .analystRatings(null)  // Missing
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
        var snapshot = completeSnapshot()
                .revenueTTM(null)  // Missing - breaks psRatio
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
        var snapshot1 = completeSnapshot()
                .currentPrice(new BigDecimal("100.00"))
                .marketCap(new BigDecimal("1000000000"))
                .build();

        monthlyReport.updateMetrics(snapshot1);

        BigDecimal firstPrice = monthlyReport.forwardPeRatio;
        BigDecimal firstPsRatio = monthlyReport.psRatio.value();

        // When: Second update with different data
        var snapshot2 = completeSnapshot()
                .currentPrice(new BigDecimal("200.00"))
                .marketCap(new BigDecimal("2000000000"))
                .forwardPeRatio(new BigDecimal("30.0"))
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
        var snapshot = completeSnapshot()
                .marketCap(null)
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
        var snapshot = completeSnapshot()
                .currentPrice(new BigDecimal("200.00"))
                .targetPrice(new BigDecimal("150.00"))
                .build();

        // When: Updating metrics
        monthlyReport.updateMetrics(snapshot);

        // Then: Upside should be negative ((150 - 200) / 200 * 100 = -25%)
        assertThat(monthlyReport.upsidePotential)
                .isNotNull()
                .satisfies(upside -> assertThat(upside.value())
                        .isCloseTo(new BigDecimal("-25.0000"), within(new BigDecimal("0.0001"))));
    }

    private static MarketDataSnapshot.MarketDataSnapshotBuilder completeSnapshot() {
        return MarketDataSnapshot.builder()
                .currentPrice(new BigDecimal("150.00"))
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(new BigDecimal("500000000"))
                .forwardPeRatio(new BigDecimal("25.0"))
                .targetPrice(new BigDecimal("180.00"))
                .forwardEpsGrowth(new BigDecimal("15.0"))
                .forwardRevenueGrowth(new BigDecimal("12.5"))
                .analystRatings(AnalystRatings.builder()
                        .strongBuy(5)
                        .buy(10)
                        .hold(3)
                        .sell(1)
                        .strongSell(0)
                        .build());
    }

    private static MarketDataSnapshot.MarketDataSnapshotBuilder avOnlySnapshot() {
        return MarketDataSnapshot.builder()
                .currentPrice(new BigDecimal("150.00"))
                .marketCap(new BigDecimal("1000000000"))
                .revenueTTM(new BigDecimal("500000000"))
                .forwardPeRatio(new BigDecimal("25.0"))
                .targetPrice(new BigDecimal("180.00"))
                // YH fields are null
                .forwardEpsGrowth(null)
                .forwardRevenueGrowth(null)
                .analystRatings(null);
    }

    private static MarketDataSnapshot.MarketDataSnapshotBuilder yhOnlySnapshot() {
        return MarketDataSnapshot.builder()
                // AV fields incomplete for complex metrics
                .currentPrice(null)
                .marketCap(null)
                .revenueTTM(null)
                .forwardPeRatio(null)
                .targetPrice(null)
                // YH fields complete
                .forwardEpsGrowth(new BigDecimal("15.0"))
                .forwardRevenueGrowth(new BigDecimal("12.5"))
                .analystRatings(AnalystRatings.builder()
                        .strongBuy(5)
                        .buy(10)
                        .hold(3)
                        .sell(1)
                        .strongSell(0)
                        .build());
    }
}
