package com.stock.screener.collector.adapter.out.web.alphavantage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
class AlphaVantageResponseLogPersistenceService {

    @Transactional
    void persistLog(String ticker, String functionName, String rawJson) {
        var logEntry = new AlphaVantageResponseLog(ticker, functionName, rawJson);
        logEntry.persist();
    }
}
