package com.stock.screener.application.port.out.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BalanceSheetResponse(
        @JsonProperty("symbol") String symbol,
        @JsonProperty("annualReports") List<BalanceSheetReport> annualReports,
        @JsonProperty("quarterlyReports") List<BalanceSheetReport> quarterlyReports
) {}
