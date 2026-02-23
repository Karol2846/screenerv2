package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.mapper.StockDataMapper;
import com.stock.screener.collector.application.port.in.CollectMonthlyDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import com.stock.screener.domain.entity.MonthlyReport;
import com.stock.screener.domain.entity.Stock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MonthlyDataCollectorService implements CollectMonthlyDataUseCase {

    private final AlphaVantageClient alphaVantageClient;
    private final YahooFinanceClient yahooFinanceClient;
    private final StockDataMapper stockDataMapper;

    @Override
    @Transactional
    public void collectMonthlyData(String ticker) {
        log.info("Monthly collection for: {}", ticker);

        Stock stock = findOrCreateStock(ticker);

        var rawOverview = alphaVantageClient.fetchOverview(ticker);
        var yhResponse = yahooFinanceClient.getQuoteSummary(ticker);

        var snapshot = stockDataMapper.toMarketDataSnapshot(rawOverview, yhResponse);

        MonthlyReport report = MonthlyReport.find("stock", stock).firstResult();
        if (report == null) {
            report = new MonthlyReport();
            report.stock = stock;
        }

        report.updateMetrics(snapshot);
        report.persist();
    }

    private Stock findOrCreateStock(String ticker) {
        Stock stock = Stock.findById(ticker);
        if (stock == null) {
            stock = new Stock();
            stock.ticker = ticker;
            stock.persist();
        }
        return stock;
    }
}
