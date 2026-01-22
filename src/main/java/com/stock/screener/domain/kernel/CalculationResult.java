package com.stock.screener.domain.kernel;

import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface CalculationResult<T> permits CalculationResult.Success, CalculationResult.Failure {

    record Success<T>(T value) implements CalculationResult<T> {

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public T getOrNull() {
            return value;
        }

        @Override
        public <U> CalculationResult<U> map(Function<T, U> mapper) {
            return new Success<>(mapper.apply(value));
        }

        @Override
        public CalculationResult<T> onSuccess(Consumer<T> action) {
            action.accept(value);
            return this;
        }

        @Override
        public CalculationResult<T> onFailure(Consumer<Failure<T>> action) {
            return this;
        }
    }

    record Failure<T>(String reason, CalculationErrorType type) implements CalculationResult<T> {

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public T getOrNull() {
            return null;
        }

        @Override
        public <U> CalculationResult<U> map(Function<T, U> mapper) {
            return new Failure<>(reason, type);
        }

        @Override
        public CalculationResult<T> onSuccess(Consumer<T> action) {
            return this;
        }

        @Override
        public CalculationResult<T> onFailure(Consumer<Failure<T>> action) {
            action.accept(this);
            return this;
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

    boolean isSuccess();

    boolean isFailure();

    T getOrNull();

    <U> CalculationResult<U> map(Function<T, U> mapper);

    CalculationResult<T> onSuccess(Consumer<T> action);

    CalculationResult<T> onFailure(Consumer<Failure<T>> action);
}

