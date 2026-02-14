package com.stock.screener.domain.service;

import com.stock.screener.domain.kernel.CalculationErrorType;
import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.valueobject.AltmanZScore;
import com.stock.screener.domain.valueobject.Sector;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("AltmanScoreCalculator Domain Service Tests")
class AltmanScoreCalculatorTest {

    private AltmanScoreCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new AltmanScoreCalculator();
    }

    @Nested
    @DisplayName("Manufacturing Sectors (Original Z-Score Formula)")
    class ManufacturingSectors {

        @ParameterizedTest(name = "Manufacturing sector {0} should compute Z-Score successfully")
        @MethodSource("com.stock.screener.domain.service.AltmanScoreCalculatorTest#manufacturingSectorsProvider")
        @DisplayName("Manufacturing sectors produce valid Z-Score")
        void testManufacturingSectorsProduceValidScore(Sector sector, BigDecimal expectedScore) {
            // Given: Complete financial snapshot for manufacturing
            var snapshot = baseSnapshot().build();

            // When: Computing Altman Z-Score for manufacturing sector
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, sector);

            // Then: Result should be successful with expected score
            assertThat(result).isInstanceOf(CalculationResult.Success.class);

            result.onSuccess(score -> assertThat(score.value())
                    .isCloseTo(expectedScore, within(new BigDecimal("0.0001"))));
        }

        @Test
        @DisplayName("Manufacturing sector with null totalRevenue should fail with MISSING_DATA")
        void testManufacturingWithNullRevenueShouldFail() {
            // Given: Snapshot with null totalRevenue (required for manufacturing)
            var snapshot = baseSnapshot()
                    .totalRevenue(null)
                    .build();

            // When: Computing Z-Score for manufacturing sector
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.ENERGY);

            // Then: Should fail with MISSING_DATA
            assertThat(result).isInstanceOf(CalculationResult.Failure.class);

            result.onFailure(failure -> {
                assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
                assertThat(failure.reason()).contains("totalRevenue");
            });
        }
    }

    @Nested
    @DisplayName("Non-Manufacturing Sectors (Z''-Score Formula)")
    class NonManufacturingSectors {

        @ParameterizedTest(name = "Non-Manufacturing sector {0} should compute Z''-Score successfully")
        @MethodSource("com.stock.screener.domain.service.AltmanScoreCalculatorTest#nonManufacturingSectorsProvider")
        @DisplayName("Non-Manufacturing sectors produce valid Z''-Score")
        void testNonManufacturingSectorsProduceValidScore(Sector sector, BigDecimal expectedScore) {
            // Given: Complete financial snapshot for non-manufacturing
            var snapshot = baseSnapshot().build();

            // When: Computing Altman Z''-Score for non-manufacturing sector
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, sector);

            // Then: Result should be successful with expected score
            assertThat(result).isInstanceOf(CalculationResult.Success.class);

            result.onSuccess(score -> assertThat(score.value())
                    .isCloseTo(expectedScore, within(new BigDecimal("0.0001"))));
        }

        @Test
        @DisplayName("Non-Manufacturing sector with null totalRevenue should succeed (revenue not required)")
        void testNonManufacturingWithNullRevenueCanSucceed() {
            // Given: Snapshot with null totalRevenue (NOT required for non-manufacturing)
            var snapshot = baseSnapshot()
                    .totalRevenue(null)
                    .build();

            // When: Computing Z''-Score for non-manufacturing sector
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.TECHNOLOGY);

            // Then: Should succeed (totalRevenue is not required for non-manufacturing formula)
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
        }
    }

    @Nested
    @DisplayName("Not Applicable Sectors")
    class NotApplicableSectors {

        @ParameterizedTest(name = "{0} sector should skip calculation (not applicable)")
        @EnumSource(value = Sector.class, names = {"FINANCE", "OTHER"})
        @DisplayName("FINANCE and OTHER sectors skip calculation")
        void testNotApplicableSectorsShouldSkip(Sector sector) {
            // Given: Valid snapshot but not applicable sector
            var snapshot = baseSnapshot().build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, sector);

            // Then: Should skip with NOT_APPLICABLE
            assertThat(result).isInstanceOf(CalculationResult.Skipped.class);

            result.onSkipped(skipped -> {
                assertThat(skipped.type()).isEqualTo(CalculationErrorType.NOT_APPLICABLE);
                assertThat(skipped.reason()).contains("not applicable");
            });
        }
    }

    @Nested
    @DisplayName("Validation Failures")
    class ValidationFailures {

        @Test
        @DisplayName("Zero totalAssets should fail with DIVISION_BY_ZERO")
        void testZeroTotalAssetsShouldFail() {
            // Given: Snapshot with zero totalAssets
            var snapshot = baseSnapshot()
                    .totalAssets(BigDecimal.ZERO)
                    .build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.TECHNOLOGY);

            // Then: Should fail with DIVISION_BY_ZERO
            assertThat(result).isInstanceOf(CalculationResult.Failure.class);

            result.onFailure(failure -> {
                assertThat(failure.type()).isEqualTo(CalculationErrorType.DIVISION_BY_ZERO);
                assertThat(failure.reason()).contains("totalAssets");
            });
        }

        @Test
        @DisplayName("Zero totalLiabilities should fail with DIVISION_BY_ZERO")
        void testZeroTotalLiabilitiesShouldFail() {
            // Given: Snapshot with zero totalLiabilities
            var snapshot = baseSnapshot()
                    .totalLiabilities(BigDecimal.ZERO)
                    .build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.TECHNOLOGY);

            // Then: Should fail with DIVISION_BY_ZERO
            assertThat(result).isInstanceOf(CalculationResult.Failure.class);

            result.onFailure(failure -> {
                assertThat(failure.type()).isEqualTo(CalculationErrorType.DIVISION_BY_ZERO);
                assertThat(failure.reason()).contains("totalLiabilities");
            });
        }

        @Test
        @DisplayName("Null totalCurrentAssets should fail with MISSING_DATA")
        void testNullTotalCurrentAssetsShouldFail() {
            // Given: Snapshot with null totalCurrentAssets
            var snapshot = baseSnapshot()
                    .totalCurrentAssets(null)
                    .build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.TECHNOLOGY);

            // Then: Should fail with MISSING_DATA
            assertThat(result).isInstanceOf(CalculationResult.Failure.class);

            result.onFailure(failure -> {
                assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
                assertThat(failure.reason()).contains("totalCurrentAssets");
            });
        }

        @Test
        @DisplayName("Null retainedEarnings should fail with MISSING_DATA")
        void testNullRetainedEarningsShouldFail() {
            // Given: Snapshot with null retainedEarnings
            var snapshot = baseSnapshot()
                    .retainedEarnings(null)
                    .build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.ENERGY);

            // Then: Should fail with MISSING_DATA
            assertThat(result).isInstanceOf(CalculationResult.Failure.class);

            result.onFailure(failure -> {
                assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
                assertThat(failure.reason()).contains("retainedEarnings");
            });
        }

        @Test
        @DisplayName("Null ebit should fail with MISSING_DATA")
        void testNullEbitShouldFail() {
            // Given: Snapshot with null ebit
            var snapshot = baseSnapshot()
                    .ebit(null)
                    .build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.MINING);

            // Then: Should fail with MISSING_DATA
            assertThat(result).isInstanceOf(CalculationResult.Failure.class);

            result.onFailure(failure -> {
                assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
                assertThat(failure.reason()).contains("ebit");
            });
        }
    }

    @Nested
    @DisplayName("Result Properties")
    class ResultProperties {

        @Test
        @DisplayName("Result should have exactly 4 decimal places (SCALE = 4)")
        void testResultPrecision() {
            // Given: Complete snapshot
            var snapshot = baseSnapshot().build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.TECHNOLOGY);

            // Then: Result should have scale of 4
            assertThat(result).isInstanceOf(CalculationResult.Success.class);

            result.onSuccess(score -> assertThat(score.value().scale()).isEqualTo(4));
        }

        @Test
        @DisplayName("Different asset values produce proportionally different Z-Scores")
        void testDifferentAssetValuesProduceDifferentScores() {
            // Given: Two snapshots with different asset values
            var snapshot1 = baseSnapshot()
                    .totalAssets(new BigDecimal("1000000"))
                    .build();

            var snapshot2 = baseSnapshot()
                    .totalAssets(new BigDecimal("2000000"))
                    .build();

            // When: Computing Z-Scores for both
            CalculationResult<AltmanZScore> result1 = calculator.calculate(snapshot1, Sector.TECHNOLOGY);
            CalculationResult<AltmanZScore> result2 = calculator.calculate(snapshot2, Sector.TECHNOLOGY);

            // Then: Scores should be different
            assertThat(result1).isInstanceOf(CalculationResult.Success.class);
            assertThat(result2).isInstanceOf(CalculationResult.Success.class);

            BigDecimal[] score1 = new BigDecimal[1];
            BigDecimal[] score2 = new BigDecimal[1];

            result1.onSuccess(score -> score1[0] = score.value());
            result2.onSuccess(score -> score2[0] = score.value());

            assertThat(score1[0]).isNotEqualTo(score2[0]);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Negative working capital should produce lower Z-Score")
        void testNegativeWorkingCapital() {
            // Given: Snapshot with current liabilities > current assets (negative working capital)
            var snapshot = baseSnapshot()
                    .totalCurrentAssets(new BigDecimal("100000"))
                    .totalCurrentLiabilities(new BigDecimal("500000"))
                    .build();

            // When: Computing Z-Score
            CalculationResult<AltmanZScore> result = calculator.calculate(snapshot, Sector.TECHNOLOGY);

            // Then: Should still calculate successfully (negative values are valid in finance)
            assertThat(result).isInstanceOf(CalculationResult.Success.class);

            result.onSuccess(score -> {
                // Z-Score should be lower due to negative working capital
                assertThat(score.value()).isEqualTo(new BigDecimal("0.1120"));
            });
        }

        @Test
        @DisplayName("High equity relative to liabilities produces higher Z-Score")
        void testHighEquityRatioProducesHigherScore() {
            // Given: Two snapshots with different equity/liabilities ratios
            var lowEquitySnapshot = baseSnapshot()
                    .totalShareholderEquity(new BigDecimal("300000"))
                    .totalLiabilities(new BigDecimal("700000"))
                    .build();

            var highEquitySnapshot = baseSnapshot()
                    .totalShareholderEquity(new BigDecimal("900000"))
                    .totalLiabilities(new BigDecimal("100000"))
                    .build();

            // When: Computing Z-Scores
            CalculationResult<AltmanZScore> lowEquityResult = calculator.calculate(lowEquitySnapshot, Sector.TECHNOLOGY);
            CalculationResult<AltmanZScore> highEquityResult = calculator.calculate(highEquitySnapshot, Sector.TECHNOLOGY);

            // Then: High equity snapshot should produce higher Z-Score
            assertThat(lowEquityResult).isInstanceOf(CalculationResult.Success.class);
            assertThat(highEquityResult).isInstanceOf(CalculationResult.Success.class);

            BigDecimal[] lowScore = new BigDecimal[1];
            BigDecimal[] highScore = new BigDecimal[1];

            lowEquityResult.onSuccess(score -> lowScore[0] = score.value());
            highEquityResult.onSuccess(score -> highScore[0] = score.value());

            assertThat(highScore[0]).isGreaterThan(lowScore[0]);
        }
    }

    // === Test Data Providers ===

    private static FinancialDataSnapshot.FinancialDataSnapshotBuilder baseSnapshot() {
        return FinancialDataSnapshot.builder()
                .totalCurrentAssets(new BigDecimal("500000"))
                .totalCurrentLiabilities(new BigDecimal("200000"))
                .totalAssets(new BigDecimal("1000000"))
                .totalLiabilities(new BigDecimal("400000"))
                .retainedEarnings(new BigDecimal("150000"))
                .ebit(new BigDecimal("100000"))
                .totalShareholderEquity(new BigDecimal("600000"))
                .totalRevenue(new BigDecimal("800000"));
    }

    static Stream<Arguments> manufacturingSectorsProvider() {
        return Stream.of(
                Arguments.of(Sector.ENERGY, new BigDecimal("2.6000")),
                Arguments.of(Sector.MINING, new BigDecimal("2.6000")),
                Arguments.of(Sector.UTILITIES, new BigDecimal("2.6000"))
        );
    }

    static Stream<Arguments> nonManufacturingSectorsProvider() {
        return Stream.of(
                Arguments.of(Sector.TECHNOLOGY, new BigDecimal("4.7040")),
                Arguments.of(Sector.HEALTHCARE, new BigDecimal("4.7040")),
                Arguments.of(Sector.CONSUMER_DISCRETIONARY, new BigDecimal("4.7040")),
                Arguments.of(Sector.REAL_ESTATE, new BigDecimal("4.7040"))
        );
    }
}

