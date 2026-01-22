package com.stock.screener.domain.kernel;

import java.time.LocalDateTime;

public record ReportError(
        String metric,
        String reason,
        CalculationErrorType errorType,
        LocalDateTime occurredAt
) {

    public static ReportError fromFailure(String metricName, CalculationResult.Failure<?> failure) {
        return new ReportError(
                metricName,
                failure.reason(),
                failure.type(),
                LocalDateTime.now()
        );
    }
}

