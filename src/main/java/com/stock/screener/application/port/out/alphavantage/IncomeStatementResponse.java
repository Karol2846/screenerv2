package com.stock.screener.application.port.out.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IncomeStatementResponse(
        @JsonProperty("symbol") String symbol,
        @JsonProperty("annualReports") List<IncomeStatementReport> annualReports,
        @JsonProperty("quarterlyReports") List<IncomeStatementReport> quarterlyReports
) {}
