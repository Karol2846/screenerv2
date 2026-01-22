package com.stock.screener.domain.kernel;

import java.time.LocalDateTime;

public record ReportError(
        String metric,
        String reason,
        CalculationErrorType errorType,
        LocalDateTime occurredAt
) {

    /**
     * Factory method do tworzenia ReportError z CalculationResult.Failure.
     */
    public static ReportError fromFailure(String metricName, CalculationResult.Failure<?> failure) {
        return new ReportError(
                metricName,
                failure.reason(),
                failure.type(),
                LocalDateTime.now()
        );
    }

    /**
     * Uproszczona factory method.
     */
    public static ReportError of(String metric, String reason, CalculationErrorType type) {
        return new ReportError(metric, reason, type, LocalDateTime.now());
    }
}

