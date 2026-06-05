package com.stock.screener.collector.domain.valueobject;

import com.stock.screener.collector.domain.kernel.CalculationErrorType;
import com.stock.screener.collector.domain.kernel.CalculationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.stock.screener.collector.domain.valueobject.InterestCoverageStatus.*;
import static com.stock.screener.collector.domain.valueobject.fixtures.FinancialDataSnapshotFixture.aFinancialDataSnapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("InterestCoverageRatio Value Object Tests")
class InterestCoverageRatioTest {

    @Nested
    @DisplayName("COVERED — EBIT ≥ 0 and interest expense > 0")
    class CoveredCases {

        @Test
        @DisplayName("Valid EBIT and interestExpense should compute ratio with COVERED status")
        void testValidDataComputesInterestCoverageRatio() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("1000000")
                    .withInterestExpense("100000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(COVERED);
                assertThat(ratio.value())
                        .isCloseTo(new BigDecimal("10.0000"), within(new BigDecimal("0.0001")));
            });
        }

        @Test
        @DisplayName("Different EBIT values produce different COVERED ratios")
        void testDifferentEbitProducesDifferentRatios() {
            // Given
            var snapshot1 = aFinancialDataSnapshot()
                    .withEbit("500000")
                    .withInterestExpense("100000")
                    .build();

            var snapshot2 = aFinancialDataSnapshot()
                    .withEbit("2000000")
                    .withInterestExpense("100000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result1 = InterestCoverageRatio.compute(snapshot1);
            CalculationResult<InterestCoverageRatio> result2 = InterestCoverageRatio.compute(snapshot2);

            // Then
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
        @DisplayName("Result should have exactly 4 decimal places (SCALE = 4)")
        void testResultPrecision() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("1000000")
                    .withInterestExpense("333333")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(COVERED);
                assertThat(ratio.value().scale()).isEqualTo(4);
            });
        }

        @Test
        @DisplayName("High EBIT relative to interest produces high COVERED ratio")
        void testHighCoverageRatio() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("10000000")
                    .withInterestExpense("100000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: 100x ratio — strong debt service
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(COVERED);
                assertThat(ratio.value())
                        .isCloseTo(new BigDecimal("100.0000"), within(new BigDecimal("0.0001")));
            });
        }

        @Test
        @DisplayName("Low EBIT relative to interest produces sub-2 COVERED ratio — dangerous territory")
        void testLowCoverageRatio() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("150000")
                    .withInterestExpense("100000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: 1.5x — barely covers interest
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(COVERED);
                assertThat(ratio.value())
                        .isCloseTo(new BigDecimal("1.5000"), within(new BigDecimal("0.0001")));
            });
        }

        @Test
        @DisplayName("EBIT less than interest expense produces sub-one COVERED ratio")
        void testEbitLessThanInterestProducesSubOneRatio() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("50000")
                    .withInterestExpense("100000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: 0.5 — cannot cover interest from operations
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(COVERED);
                assertThat(ratio.value())
                        .isCloseTo(new BigDecimal("0.5000"), within(new BigDecimal("0.0001")));
            });
        }

        @Test
        @DisplayName("Debt-free company with lease interest: small positive interestExpense still yields COVERED (not NO_DEBT)")
        void testCompanyWithLeaseInterestYieldsCovered() {
            // Given: company with no financial debt but paying lease interest (like MNST)
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("729958000")
                    .withInterestExpense("600000")   // small lease interest
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: ratio is ~1216x — valid and meaningful, NOT NO_DEBT
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(COVERED);
                assertThat(ratio.value()).isNotNull();
                assertThat(ratio.value()).isGreaterThan(new BigDecimal("1000"));
            });
        }
    }

    @Nested
    @DisplayName("OPERATING_LOSS — EBIT < 0 with positive interest expense")
    class OperatingLossCases {

        @Test
        @DisplayName("Negative EBIT with positive interestExpense produces OPERATING_LOSS, not a negative ratio")
        void testNegativeEbitProducesOperatingLoss() {
            // Given: operating loss (like RIVN)
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("-500000")
                    .withInterestExpense("100000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: Success but with OPERATING_LOSS flag, value is null (ratio would be misleading)
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(OPERATING_LOSS);
                assertThat(ratio.value()).isNull();
            });
        }

        @Test
        @DisplayName("Large operating loss with significant interest also yields OPERATING_LOSS")
        void testLargeOperatingLoss() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("-881000000")   // RIVN Q1 2026
                    .withInterestExpense("65000000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(OPERATING_LOSS);
                assertThat(ratio.value()).isNull();
            });
        }
    }

    @Nested
    @DisplayName("NO_DEBT — interest expense is zero or null")
    class NoDebtCases {

        @Test
        @DisplayName("Null interestExpense yields NO_DEBT — not a missing-data error")
        void testNullInterestExpenseYieldsNoDebt() {
            // Given: company with no interest obligations
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("1000000")
                    .withNullInterestExpense()
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: Success, positive NO_DEBT signal, value null (nothing to cover)
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(NO_DEBT);
                assertThat(ratio.value()).isNull();
            });
        }

        @Test
        @DisplayName("Zero interestExpense yields NO_DEBT — not DIVISION_BY_ZERO error")
        void testZeroInterestExpenseYieldsNoDebt() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("1000000")
                    .withInterestExpense("0")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: Success, NO_DEBT, not an error
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(NO_DEBT);
                assertThat(ratio.value()).isNull();
            });
        }
    }

    @Nested
    @DisplayName("Precedence — NO_DEBT beats OPERATING_LOSS when both conditions hold")
    class PrecedenceCases {

        @Test
        @DisplayName("Negative EBIT AND null interestExpense → NO_DEBT wins (nothing to cover anyway)")
        void testNegativeEbitAndNullInterestYieldsNoDebt() {
            // Given: early-stage biotech — operating loss but zero debt
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("-200000")
                    .withNullInterestExpense()
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: NO_DEBT takes precedence — debt coverage question is moot
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(NO_DEBT);
                assertThat(ratio.value()).isNull();
            });
        }

        @Test
        @DisplayName("Negative EBIT AND zero interestExpense → NO_DEBT wins")
        void testNegativeEbitAndZeroInterestYieldsNoDebt() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withEbit("-200000")
                    .withInterestExpense("0")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then
            assertThat(result).isInstanceOf(CalculationResult.Success.class);
            result.onSuccess(ratio -> {
                assertThat(ratio.status()).isEqualTo(NO_DEBT);
                assertThat(ratio.value()).isNull();
            });
        }
    }

    @Nested
    @DisplayName("MISSING_DATA — null EBIT is the only true error")
    class MissingDataCases {

        @Test
        @DisplayName("Null EBIT should fail with MISSING_DATA — cannot determine any status")
        void testNullEbitShouldFail() {
            // Given
            var snapshot = aFinancialDataSnapshot()
                    .withNullEbit()
                    .withInterestExpense("100000")
                    .build();

            // When
            CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

            // Then: Failure — we cannot classify without EBIT
            assertThat(result).isInstanceOf(CalculationResult.Failure.class);
            result.onFailure(failure -> {
                assertThat(failure.type()).isEqualTo(CalculationErrorType.MISSING_DATA);
                assertThat(failure.reason()).contains("ebit");
            });
        }
    }
}
