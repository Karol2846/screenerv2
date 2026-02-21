package com.stock.screener.collector.application.port.out.yhfinance;

import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;

public interface YahooFinanceClient {

    YhFinanceResponse getQuoteSummary(String ticker);
}
