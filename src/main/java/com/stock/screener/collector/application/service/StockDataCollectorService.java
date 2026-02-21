package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.mapper.StockDataMapper;
import com.stock.screener.collector.application.port.in.CollectStockDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import com.stock.screener.domain.entity.MonthlyReport;
import com.stock.screener.domain.entity.QuarterlyReport;
import com.stock.screener.domain.entity.Stock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StockDataCollectorService implements CollectStockDataUseCase {

    private final AlphaVantageClient alphaVantageClient;
    private final YahooFinanceClient yahooFinanceClient;
    private final TickerReaderPort tickerReaderPort;
    private final StockDataMapper stockDataMapper;

    @Override
    @Transactional
    public Stock collectDataForStock(String ticker) {
        log.info("Starting data collection for: {}", ticker);

        Stock stock = Stock.findById(ticker);
        if (stock == null) {
            stock = new Stock();
            stock.ticker = ticker;
            stock.persist();
        }

        var rawOverview = alphaVantageClient.fetchOverview(ticker);
        var yhResponse = yahooFinanceClient.getQuoteSummary(ticker);

        var snapshot = stockDataMapper.toMarketDataSnapshot(rawOverview, yhResponse);

        stock.marketData = stockDataMapper.toMarketData(snapshot);

        MonthlyReport report = MonthlyReport.find("stock", stock).firstResult();
        if (report == null) {
            report = new MonthlyReport();
            report.stock = stock;
        }

        report.updateMetrics(snapshot);
        report.persist();

        var rawBalance = alphaVantageClient.fetchBalanceSheet(ticker);
        var rawIncome = alphaVantageClient.fetchIncomeStatement(ticker);
        var rawCash = alphaVantageClient.fetchCashFlow(ticker);

        var financialSnapshot = stockDataMapper.toFinancialDataSnapshot(rawBalance, rawIncome, rawCash);

        QuarterlyReport qReport = QuarterlyReport.find("stock", stock).firstResult();
        if (qReport == null) {
            qReport = new QuarterlyReport();
            qReport.stock = stock;
            qReport.fiscalDateEnding = java.time.LocalDate.now(); // Dummy, docelowo z raportu
        }

        qReport.updateMetrics(financialSnapshot, stock.sector);
        qReport.persist();

        return stock;
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
