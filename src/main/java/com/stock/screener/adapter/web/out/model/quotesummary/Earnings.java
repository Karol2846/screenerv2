package com.stock.screener.adapter.web.out.model.quotesummary;

import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Dane o zyskach (earnings) firmy.
 */
@Builder
@Jacksonized
public record Earnings(
        EarningsChart earningsChart,
        FinancialsChart financialsChart,
        Integer maxAge
) {

    @Builder
    @Jacksonized
    public record EarningsChart(
            List<QuarterlyEarnings> quarterly,
            FormattedValue currentQuarterEstimate,
            String currentQuarterEstimateDate,
            Integer currentQuarterEstimateYear,
            List<FormattedValue> earningsDate
    ) {
    }

    @Builder
    @Jacksonized
    public record QuarterlyEarnings(
            String date,
            FormattedValue actual,
            FormattedValue estimate
    ) {
    }

    @Builder
    @Jacksonized
    public record FinancialsChart(
            List<YearlyFinancials> yearly,
            List<QuarterlyFinancials> quarterly
    ) {
    }

    @Builder
    @Jacksonized
    public record YearlyFinancials(
            Integer date,
            FormattedValue revenue,
            FormattedValue earnings
    ) {
    }

    @Builder
    @Jacksonized
    public record QuarterlyFinancials(
            String date,
            FormattedValue revenue,
            FormattedValue earnings
    ) {
    }
}

