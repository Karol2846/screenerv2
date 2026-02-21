package com.stock.screener.collector.application.port.out.alphavantage;

public interface AlphaVantageClient {

    RawOverview fetchOverview(String ticker);

    RawBalanceSheet fetchBalanceSheet(String ticker);

    RawIncomeStatement fetchIncomeStatement(String ticker);
}
