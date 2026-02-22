package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.mapper.StockDataMapper;
import com.stock.screener.collector.application.port.in.CollectStockDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import com.stock.screener.domain.entity.MonthlyReport;
import com.stock.screener.domain.entity.QuarterlyReport;
import com.stock.screener.domain.entity.Stock;
import com.stock.screener.domain.valueobject.Sector;
import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;
import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

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

        Stock stock = findOrCreateStock(ticker);

        var rawOverview = alphaVantageClient.fetchOverview(ticker);
        var yhResponse = yahooFinanceClient.getQuoteSummary(ticker);

        updateMarketData(stock, rawOverview, yhResponse);
        updateFinancialData(stock, ticker);

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

    private Stock findOrCreateStock(String ticker) {
        Stock stock = Stock.findById(ticker);
        if (stock == null) {
            stock = new Stock();
            stock.ticker = ticker;
            stock.persist();
        }
        return stock;
    }

    private void updateMarketData(Stock stock, RawOverview rawOverview, YhFinanceResponse yhResponse) {
        if (rawOverview != null && rawOverview.sector() != null) {
            stock.sector = Sector.fromString(rawOverview.sector());
        }

        var snapshot = stockDataMapper.toMarketDataSnapshot(rawOverview, yhResponse);
        stock.marketData = stockDataMapper.toMarketData(snapshot);

        MonthlyReport report = MonthlyReport.find("stock", stock).firstResult();
        if (report == null) {
            report = new MonthlyReport();
            report.stock = stock;
        }

        report.updateMetrics(snapshot);
        report.persist();
    }

    private void updateFinancialData(Stock stock, String ticker) {
        var rawBalance = alphaVantageClient.fetchBalanceSheet(ticker);
        var rawIncome = alphaVantageClient.fetchIncomeStatement(ticker);
        var rawCash = alphaVantageClient.fetchCashFlow(ticker);

        var financialSnapshot = stockDataMapper.toFinancialDataSnapshot(rawBalance, rawIncome, rawCash);

        QuarterlyReport qReport = QuarterlyReport.find("stock", stock).firstResult();
        if (qReport == null) {
            qReport = new QuarterlyReport();
            qReport.stock = stock;
            qReport.fiscalDateEnding = resolveFiscalDate(rawBalance);
        }

        qReport.updateMetrics(financialSnapshot, stock.sector);
        qReport.persist();
    }

    private LocalDate resolveFiscalDate(RawBalanceSheet rawBalance) {
        if (rawBalance != null && rawBalance.quarterlyReports() != null && !rawBalance.quarterlyReports().isEmpty()) {
            String dateStr = rawBalance.quarterlyReports().getFirst().fiscalDateEnding();
            if (dateStr != null) {
                return LocalDate.parse(dateStr);
            }
        }
        return LocalDate.now();
    }
}
