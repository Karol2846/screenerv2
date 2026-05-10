package com.stock.screener.collector.domain.kernel;

import java.util.function.Consumer;

public sealed interface CalculationResult<T> permits CalculationResult.Success, CalculationResult.Failure, CalculationResult.Skipped {

    record Success<T>(T value) implements CalculationResult<T> {

        @Override
        public CalculationResult<T> onSuccess(Consumer<T> action) {
            action.accept(value);
            return this;
        }

        @Override
        public CalculationResult<T> onFailure(Consumer<Failure<T>> action) {
            return this;
        }

        @Override
        public void onSkipped(Consumer<Skipped<T>> action) {
        }
    }

    record Failure<T>(String reason, CalculationErrorType type) implements CalculationResult<T> {

        @Override
        public CalculationResult<T> onSuccess(Consumer<T> action) {
            return this;
        }

        @Override
        public CalculationResult<T> onFailure(Consumer<Failure<T>> action) {
            action.accept(this);
            return this;
        }

        @Override
        public void onSkipped(Consumer<Skipped<T>> action) {
        }
    }

    record Skipped<T>(String reason) implements CalculationResult<T> {

        @Override
        public CalculationResult<T> onSuccess(Consumer<T> action) {
            return this;
        }

        @Override
        public CalculationResult<T> onFailure(Consumer<Failure<T>> action) {
            return this;
        }

        @Override
        public void onSkipped(Consumer<Skipped<T>> action) {
            action.accept(this);
        }
    }

    // --- Factory methods ---

    static <T> CalculationResult<T> success(T value) {
        return new Success<>(value);
    }

    static <T> CalculationResult<T> failure(String reason, CalculationErrorType type) {
        return new Failure<>(reason, type);
    }

    static <T> CalculationResult<T> missingData(String fieldName) {
        return new Failure<>(
                fieldName + "data is missing",
                CalculationErrorType.MISSING_DATA
        );
    }

    static <T> CalculationResult<T> divisionByZero(String denominatorName) {
        return new Failure<>(
                denominatorName + "is null or 0",
                CalculationErrorType.DIVISION_BY_ZERO
        );
    }

    static <T> CalculationResult<T> skip(String reason) {
        return new Skipped<>(reason);
    }

    CalculationResult<T> onSuccess(Consumer<T> action);

    CalculationResult<T> onFailure(Consumer<Failure<T>> action);

    void onSkipped(Consumer<Skipped<T>> action);
}

