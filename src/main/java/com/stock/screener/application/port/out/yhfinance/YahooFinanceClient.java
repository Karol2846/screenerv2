package com.stock.screener.application.port.out.yhfinance;

import com.stock.screener.application.port.out.yhfinance.command.QuoteSummaryCommand;

public interface YahooFinanceClient {

    QuoteSummaryCommand getQuoteSummary(String ticker);
}
