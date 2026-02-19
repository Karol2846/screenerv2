package com.stock.screener.adapter.web.out.alphavantage;

import com.stock.screener.application.port.out.alphavantage.*;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
class FinancialDataAssembler {

    FinancialDataSnapshot assemble(OverviewResponse overview,
                                   BalanceSheetResponse balanceSheet,
                                   IncomeStatementResponse incomeStatement) {

        BalanceSheetReport latestBs = getLatestReport(balanceSheet.annualReports());
        IncomeStatementReport matchedIs = findMatchingIncomeStatement(
                incomeStatement.annualReports(), latestBs.fiscalDateEnding());

        return FinancialDataSnapshot.builder()
                .marketCapitalization(overview.marketCapitalization())
                .totalCurrentAssets(latestBs.totalCurrentAssets())
                .totalCurrentLiabilities(latestBs.totalCurrentLiabilities())
                .totalAssets(latestBs.totalAssets())
                .totalLiabilities(latestBs.totalLiabilities())
                .retainedEarnings(nullToZero(latestBs.retainedEarnings()))
                .totalShareholderEquity(latestBs.totalShareholderEquity())
                .inventory(nullToZero(latestBs.inventory()))
                .ebit(matchedIs.ebit())
                .interestExpense(matchedIs.interestExpense())
                .totalRevenue(matchedIs.totalRevenue())
                .build();
    }

    private BalanceSheetReport getLatestReport(List<BalanceSheetReport> reports) {
        if (reports == null || reports.isEmpty()) {
            throw new IllegalArgumentException("No annual balance sheet reports available");
        }
        return reports.getFirst();
    }

    private IncomeStatementReport findMatchingIncomeStatement(List<IncomeStatementReport> reports,
                                                              String fiscalDateEnding) {
        if (reports == null || reports.isEmpty()) {
            throw new IllegalArgumentException("No annual income statement reports available");
        }
        return reports.stream()
                .filter(report -> fiscalDateEnding.equals(report.fiscalDateEnding()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No income statement found matching fiscal date: " + fiscalDateEnding));
    }

    private static BigDecimal nullToZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
