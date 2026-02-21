package com.stock.screener.collector.adapter.out.web.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CashFlowResponse(
                String symbol,
                List<CashFlowReport> annualReports,
                List<CashFlowReport> quarterlyReports) {
}
