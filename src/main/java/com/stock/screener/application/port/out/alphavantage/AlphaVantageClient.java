package com.stock.screener.application.port.out.alphavantage;

public interface AlphaVantageClient {

    OverviewResponse fetchOverview(String ticker);

    BalanceSheetResponse fetchBalanceSheet(String ticker);

    IncomeStatementResponse fetchIncomeStatement(String ticker);
}
