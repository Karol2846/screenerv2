package com.stock.screener.application.port.out.alphavantage;

import java.math.BigDecimal;
import java.util.List;

public record RawIncomeStatement(
        String symbol,
        List<Report> annualReports,
        List<Report> quarterlyReports
) {
    public record Report(
            String fiscalDateEnding,
            String reportedCurrency,
            BigDecimal grossProfit,
            BigDecimal totalRevenue,
            BigDecimal costOfRevenue,
            BigDecimal costOfGoodsAndServicesSold,
            BigDecimal operatingIncome,
            BigDecimal sellingGeneralAndAdministrative,
            BigDecimal researchAndDevelopment,
            BigDecimal operatingExpenses,
            BigDecimal netIncome,
            BigDecimal ebit,
            BigDecimal ebitda,
            BigDecimal depreciationAndAmortization,
            BigDecimal interestIncome,
            BigDecimal interestExpense,
            BigDecimal incomeTaxExpense,
            BigDecimal incomeBeforeTax,
            BigDecimal netIncomeFromContinuingOperations
    ) {}
}
