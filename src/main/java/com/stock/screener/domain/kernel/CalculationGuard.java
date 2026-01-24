package com.stock.screener.domain.kernel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class CalculationGuard<T> {

    private final T snapshot;
    private final List<ValidationError> errors;

    private CalculationGuard(T snapshot) {
        this.snapshot = snapshot;
        this.errors = new ArrayList<>();
    }

    public static <T> CalculationGuard<T> check(T snapshot) {
        return new CalculationGuard<>(snapshot);
    }

    /**
     * Requires that the field extracted by getter is not null.
     *
     * @param fieldName Human-readable field name for error messages
     * @param getter    Function to extract the field value
     * @return this for chaining
     */
    public CalculationGuard<T> require(String fieldName, Function<T, BigDecimal> getter) {
        BigDecimal value = getter.apply(snapshot);
        if (value == null) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.MISSING_DATA));
        }
        return this;
    }

    /**
     * Requires that the field value is not null and not zero (for denominators).
     *
     * @param fieldName Human-readable field name for error messages
     * @param getter    Function to extract the field value
     * @return this for chaining
     */
    public CalculationGuard<T> ensureNonZero(String fieldName, Function<T, BigDecimal> getter) {
        BigDecimal value = getter.apply(snapshot);
        if (value == null) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.MISSING_DATA));
        } else if (value.compareTo(BigDecimal.ZERO) == 0) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.DIVISION_BY_ZERO));
        }
        return this;
    }

    /**
     * Requires that the field value is positive (> 0).
     *
     * @param fieldName Human-readable field name for error messages
     * @param getter    Function to extract the field value
     * @return this for chaining
     */
    public CalculationGuard<T> requirePositive(String fieldName, Function<T, BigDecimal> getter) {
        BigDecimal value = getter.apply(snapshot);
        if (value == null) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.MISSING_DATA));
        } else if (value.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.NEGATIVE_VALUE));
        }
        return this;
    }

    /**
     * Requires that the field value is non-negative (>= 0).
     *
     * @param fieldName Human-readable field name for error messages
     * @param getter    Function to extract the field value
     * @return this for chaining
     */
    public CalculationGuard<T> requireNonNegative(String fieldName, Function<T, BigDecimal> getter) {
        BigDecimal value = getter.apply(snapshot);
        if (value == null) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.MISSING_DATA));
        } else if (value.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.NEGATIVE_VALUE));
        }
        return this;
    }

    public <R> CalculationResult<R> validate(Function<T, R> calculation) {
        if (!errors.isEmpty()) {
            ValidationError firstError = errors.getFirst();
            return switch (firstError.type()) {
                case MISSING_DATA -> CalculationResult.missingData(firstError.fieldName());
                case DIVISION_BY_ZERO -> CalculationResult.divisionByZero(firstError.fieldName());
                default -> CalculationResult.failure(
                        firstError.fieldName() + " validation failed",
                        firstError.type()
                );
            };
        }
        return CalculationResult.success(calculation.apply(snapshot));
    }

    /**
     * Returns all validation errors (for logging/debugging).
     *
     * @return List of all collected errors
     */
    public List<ValidationError> getErrors() {
        return List.copyOf(errors);
    }

    /**
     * Checks if validation passed without errors.
     *
     * @return true if no errors
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Internal record for tracking validation errors.
     */
    public record ValidationError(String fieldName, CalculationErrorType type) {
    }
}

