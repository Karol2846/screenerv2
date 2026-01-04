package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record EpsTrend(
        RawFmtValue current,
        @JsonProperty("7daysAgo") RawFmtValue sevenDaysAgo,
        @JsonProperty("30daysAgo") RawFmtValue thirtyDaysAgo,
        @JsonProperty("60daysAgo") RawFmtValue sixtyDaysAgo,
        @JsonProperty("90daysAgo") RawFmtValue ninetyDaysAgo,
        String epsTrendCurrency
) {
}

