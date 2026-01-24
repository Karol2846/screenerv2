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

    public CalculationGuard<T> require(String fieldName, Function<T, BigDecimal> getter) {
        BigDecimal value = getter.apply(snapshot);
        if (value == null) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.MISSING_DATA));
        }
        return this;
    }

    public CalculationGuard<T> ensureNonZero(String fieldName, Function<T, BigDecimal> getter) {
        BigDecimal value = getter.apply(snapshot);
        if (value == null) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.MISSING_DATA));
        } else if (value.compareTo(BigDecimal.ZERO) == 0) {
            errors.add(new ValidationError(fieldName, CalculationErrorType.DIVISION_BY_ZERO));
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

    public record ValidationError(String fieldName, CalculationErrorType type) {
    }
}

