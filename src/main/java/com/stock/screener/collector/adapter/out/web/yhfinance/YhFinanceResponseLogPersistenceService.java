package com.stock.screener.collector.adapter.out.web.yhfinance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
class YhFinanceResponseLogPersistenceService {

    @Transactional
    void persistLog(String ticker, String rawJson) {
        new YhFinanceResponseLog(ticker, rawJson).persist();
    }
}
