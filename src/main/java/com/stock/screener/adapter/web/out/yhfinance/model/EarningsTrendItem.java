package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record EarningsTrendItem(
        Integer maxAge,
        String period,
        String endDate,
        RawFmtValue growth,
        EarningsEstimate earningsEstimate,
        RevenueEstimate revenueEstimate,
        EpsTrend epsTrend,
        EpsRevisions epsRevisions
) {
}

