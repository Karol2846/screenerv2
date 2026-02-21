package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.port.in.CollectStockDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import com.stock.screener.domain.entity.Stock;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StockDataCollectorService implements CollectStockDataUseCase {

    private final AlphaVantageClient alphaVantageClient;
    private final YahooFinanceClient yahooFinanceClient;
    private final com.stock.screener.collector.application.port.out.file.TickerReaderPort tickerReaderPort;

    @Override
    public Stock collectDataForStock(String ticker) {
        log.info("Starting data collection for: {}", ticker);
        return null;
    }

    public void collectDataForAllTickers() {
        log.info("Starting collection pipeline for all tickers");

        tickerReaderPort.readTickers().forEach(ticker -> {
            try {
                collectDataForStock(ticker);
            } catch (Exception ex) {
                log.error("Failed to collect and update data for ticker: {}", ticker, ex);
            }
        });

        log.info("Collection pipeline finished.");
    }
}
