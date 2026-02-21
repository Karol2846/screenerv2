package com.stock.screener.collector.adapter.out.web.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IncomeStatementReport(
        @JsonProperty("fiscalDateEnding") String fiscalDateEnding,
        @JsonProperty("reportedCurrency") String reportedCurrency,
        @JsonProperty("grossProfit") BigDecimal grossProfit,
        @JsonProperty("totalRevenue") BigDecimal totalRevenue,
        @JsonProperty("costOfRevenue") BigDecimal costOfRevenue,
        @JsonProperty("costofGoodsAndServicesSold") BigDecimal costOfGoodsAndServicesSold,
        @JsonProperty("operatingIncome") BigDecimal operatingIncome,
        @JsonProperty("sellingGeneralAndAdministrative") BigDecimal sellingGeneralAndAdministrative,
        @JsonProperty("researchAndDevelopment") BigDecimal researchAndDevelopment,
        @JsonProperty("operatingExpenses") BigDecimal operatingExpenses,
        @JsonProperty("netIncome") BigDecimal netIncome,
        @JsonProperty("ebit") BigDecimal ebit,
        @JsonProperty("ebitda") BigDecimal ebitda,
        @JsonProperty("depreciationAndAmortization") BigDecimal depreciationAndAmortization,
        @JsonProperty("interestIncome") BigDecimal interestIncome,
        @JsonProperty("interestExpense") BigDecimal interestExpense,
        @JsonProperty("incomeTaxExpense") BigDecimal incomeTaxExpense,
        @JsonProperty("incomeBeforeTax") BigDecimal incomeBeforeTax,
        @JsonProperty("netIncomeFromContinuingOperations") BigDecimal netIncomeFromContinuingOperations
) {}
