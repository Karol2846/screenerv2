package com.stock.screener.domain.valueobject;

import com.stock.screener.domain.kernel.ReportError;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

/**
 * Collection of Scoring Engine results for the analyzed stock.
 */
@Getter
@Builder
public class AnalysisReport {
    private final String ticker;

    // Total points scored by the stock
    private final int totalScore;

    // Max possible points taking into account missing missing metrics
    private final int maxPossibleScore;

    // Reason for immediate rejection (e.g. low Altman Z-score or bad quick ratio)
    private final String rejectReason;

    // Ratings breakdown
    private final AnalystRatings analystRecommendation;

    // List of warnings or interesting findings (e.g., speculation bubble)
    private final List<String> anomalies;

    // Calculation errors that occurred during metrics building
    private final Set<ReportError> metricErrors;

    public boolean isRejected() {
        return rejectReason != null;
    }
}
