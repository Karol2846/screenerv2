package com.stock.screener.collector.adapter.out.web.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BalanceSheetReport(
        @JsonProperty("fiscalDateEnding") String fiscalDateEnding,
        @JsonProperty("reportedCurrency") String reportedCurrency,
        @JsonProperty("totalAssets") BigDecimal totalAssets,
        @JsonProperty("totalCurrentAssets") BigDecimal totalCurrentAssets,
        @JsonProperty("totalNonCurrentAssets") BigDecimal totalNonCurrentAssets,
        @JsonProperty("totalLiabilities") BigDecimal totalLiabilities,
        @JsonProperty("totalCurrentLiabilities") BigDecimal totalCurrentLiabilities,
        @JsonProperty("totalNonCurrentLiabilities") BigDecimal totalNonCurrentLiabilities,
        @JsonProperty("totalShareholderEquity") BigDecimal totalShareholderEquity,
        @JsonProperty("retainedEarnings") BigDecimal retainedEarnings,
        @JsonProperty("commonStock") BigDecimal commonStock,
        @JsonProperty("cashAndCashEquivalentsAtCarryingValue") BigDecimal cashAndCashEquivalents,
        @JsonProperty("cashAndShortTermInvestments") BigDecimal cashAndShortTermInvestments,
        @JsonProperty("inventory") BigDecimal inventory,
        @JsonProperty("currentNetReceivables") BigDecimal currentNetReceivables,
        @JsonProperty("shortTermDebt") BigDecimal shortTermDebt,
        @JsonProperty("longTermDebt") BigDecimal longTermDebt,
        @JsonProperty("currentLongTermDebt") BigDecimal currentLongTermDebt,
        @JsonProperty("longTermDebtNoncurrent") BigDecimal longTermDebtNoncurrent,
        @JsonProperty("shortLongTermDebtTotal") BigDecimal shortLongTermDebtTotal,
        @JsonProperty("commonStockSharesOutstanding") BigDecimal commonStockSharesOutstanding,
        @JsonProperty("additionalPaidInCapital") BigDecimal additionalPaidInCapital
) {}
