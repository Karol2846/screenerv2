package com.stock.screener.adapter.web.out.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BalanceSheetResponse(
        @JsonProperty("symbol") String symbol,
        @JsonProperty("annualReports") List<BalanceSheetReport> annualReports,
        @JsonProperty("quarterlyReports") List<BalanceSheetReport> quarterlyReports
) {}
