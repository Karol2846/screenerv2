package com.stock.screener.application.port.out.command;

public interface YahooFinanceClient {

    QuoteSummaryCommand getQuoteSummary(String ticker);
}
