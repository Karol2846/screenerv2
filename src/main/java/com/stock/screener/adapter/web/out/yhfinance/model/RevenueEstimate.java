package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record RevenueEstimate(
        RawFmtValue avg,
        RawFmtValue low,
        RawFmtValue high,
        RawFmtValue numberOfAnalysts,
        RawFmtValue yearAgoRevenue,
        RawFmtValue growth,
        String revenueCurrency
) {
}

