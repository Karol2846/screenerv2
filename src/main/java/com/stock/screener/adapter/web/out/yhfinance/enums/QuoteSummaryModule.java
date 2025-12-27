package com.stock.screener.adapter.web.out.yhfinance.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QuoteSummaryModule {
    ESG_SCORES("esgScores"),
    QUOTE_TYPE("quoteType"),
    PRICE("price"),
    SEC_FILINGS("secFilings"),
    INSIDER_TRANSACTIONS("insiderTransactions"),
    INSIDER_HOLDERS("insiderHolders"),
    INSTITUTION_OWNERSHIP("institutionOwnership"),
    UPGRADE_DOWNGRADE_HISTORY("upgradeDowngradeHistory"),
    RECOMMENDATION_TREND("recommendationTrend"),
    EARNINGS_TREND("earningsTrend"),
    EARNINGS_HISTORY("earningsHistory"),
    EARNINGS("earnings"),
    CASHFLOW_STATEMENT_HISTORY_QUARTERLY("cashflowStatementHistoryQuarterly"),
    CASHFLOW_STATEMENT_HISTORY("cashflowStatementHistory"),
    BALANCE_SHEET_HISTORY_QUARTERLY("balanceSheetHistoryQuarterly"),
    BALANCE_SHEET_HISTORY("balanceSheetHistory"),
    INCOME_STATEMENT_HISTORY_QUARTERLY("incomeStatementHistoryQuarterly"),
    INCOME_STATEMENT_HISTORY("incomeStatementHistory"),
    CALENDAR_EVENTS("calendarEvents"),
    DEFAULT_KEY_STATISTICS("defaultKeyStatistics"),
    FINANCIAL_DATA("financialData"),
    FUND_PROFILE("fundProfile"),
    ASSET_PROFILE("assetProfile"),
    SUMMARY_DETAIL("summaryDetail");

    private final String value;
}


