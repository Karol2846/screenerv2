package com.stock.screener.adapter.web.out.model.quotesummary;

import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wydarzenia kalendarzowe (dywidendy, earnings, itp.).
 */
@Builder
@Jacksonized
public record CalendarEvents(
        FormattedValue exDividendDate,
        FormattedValue dividendDate,
        EarningsInfo earnings,
        Integer maxAge
) {

    @Builder
    @Jacksonized
    public record EarningsInfo(
            FormattedValue earningsDate,
            FormattedValue earningsAverage,
            FormattedValue earningsLow,
            FormattedValue earningsHigh,
            FormattedValue revenueAverage,
            FormattedValue revenueLow,
            FormattedValue revenueHigh,
            List<FormattedValue> earningsCallDate
    ) {
    }
}

