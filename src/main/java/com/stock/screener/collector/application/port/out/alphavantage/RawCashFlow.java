package com.stock.screener.collector.application.port.out.alphavantage;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record RawCashFlow(
        String symbol,
        List<Report> annualReports,
        List<Report> quarterlyReports) {
    @Builder
    public record Report(
            String fiscalDateEnding,
            String reportedCurrency,
            BigDecimal operatingCashflow,
            BigDecimal paymentsForOperatingActivities,
            BigDecimal proceedsFromOperatingActivities,
            BigDecimal changeInOperatingLiabilities,
            BigDecimal changeInOperatingAssets,
            BigDecimal depreciationDepletionAndAmortization,
            BigDecimal capitalExpenditures,
            BigDecimal changeInReceivables,
            BigDecimal changeInInventory,
            BigDecimal profitLoss,
            BigDecimal cashflowFromInvestment,
            BigDecimal cashflowFromFinancing,
            BigDecimal proceedsFromRepaymentsOfShortTermDebt,
            BigDecimal paymentsForRepurchaseOfCommonStock,
            BigDecimal paymentsForRepurchaseOfEquity,
            BigDecimal paymentsForRepurchaseOfPreferredStock,
            BigDecimal dividendPayout,
            BigDecimal dividendPayoutCommonStock,
            BigDecimal dividendPayoutPreferredStock,
            BigDecimal proceedsFromIssuanceOfCommonStock,
            BigDecimal proceedsFromIssuanceOfLongTermDebtAndCapitalSecuritiesNet,
            BigDecimal proceedsFromIssuanceOfPreferredStock,
            BigDecimal proceedsFromRepurchaseOfEquity,
            BigDecimal proceedsFromSaleOfTreasuryStock,
            BigDecimal changeInCashAndCashEquivalents,
            BigDecimal changeInExchangeRate,
            BigDecimal netIncome) {
    }
}
