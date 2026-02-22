package com.stock.screener.collector.adapter.out.web.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record PriceSummary(
        BigDecimal regularMarketPrice
) {}
