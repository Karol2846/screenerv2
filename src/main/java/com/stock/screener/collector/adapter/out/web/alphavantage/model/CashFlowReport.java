package com.stock.screener.collector.adapter.out.web.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CashFlowReport(
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
