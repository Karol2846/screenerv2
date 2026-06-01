package com.stock.screener.collector.adapter.out.web.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CashFlowResponse(
        @JsonProperty("symbol") String symbol,
        @JsonProperty("Note") String note,
        @JsonProperty("Information") String information,
        @JsonProperty("Error Message") String errorMessage,
        @JsonProperty("annualReports") List<CashFlowReport> annualReports,
        @JsonProperty("quarterlyReports") List<CashFlowReport> quarterlyReports
) implements AlphaVantageResponse {}
