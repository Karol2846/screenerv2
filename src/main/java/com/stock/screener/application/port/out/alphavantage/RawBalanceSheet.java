package com.stock.screener.application.port.out.alphavantage;

import java.math.BigDecimal;
import java.util.List;

public record RawBalanceSheet(
        String symbol,
        List<Report> annualReports,
        List<Report> quarterlyReports
) {
    public record Report(
            String fiscalDateEnding,
            String reportedCurrency,
            BigDecimal totalAssets,
            BigDecimal totalCurrentAssets,
            BigDecimal totalNonCurrentAssets,
            BigDecimal totalLiabilities,
            BigDecimal totalCurrentLiabilities,
            BigDecimal totalNonCurrentLiabilities,
            BigDecimal totalShareholderEquity,
            BigDecimal retainedEarnings,
            BigDecimal commonStock,
            BigDecimal cashAndCashEquivalents,
            BigDecimal cashAndShortTermInvestments,
            BigDecimal inventory,
            BigDecimal currentNetReceivables,
            BigDecimal shortTermDebt,
            BigDecimal longTermDebt,
            BigDecimal currentLongTermDebt,
            BigDecimal longTermDebtNoncurrent,
            BigDecimal shortLongTermDebtTotal,
            BigDecimal commonStockSharesOutstanding
    ) {}
}
