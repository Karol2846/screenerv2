package com.stock.screener.domain.kernel;

import java.time.LocalDateTime;

public record ReportError(
        MetricType metric,
        String reason,
        CalculationErrorType errorType,
        LocalDateTime occurredAt
) {

    public static ReportError fromFailure(MetricType metric, CalculationResult.Failure<?> failure) {
        return new ReportError(
                metric,
                failure.reason(),
                failure.type(),
                LocalDateTime.now()
        );
    }
}

