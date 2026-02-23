package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.mapper.StockDataMapper;
import com.stock.screener.collector.application.port.in.CollectQuarterlyDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.domain.entity.QuarterlyReport;
import com.stock.screener.domain.entity.Stock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarterlyDataCollectorService implements CollectQuarterlyDataUseCase {

    private final AlphaVantageClient alphaVantageClient;
    private final StockDataMapper stockDataMapper;

    @Override
    @Transactional
    public void collectQuarterlyData(String ticker) {
        log.info("Quarterly collection for: {}", ticker);

        Stock stock = findOrCreateStock(ticker);

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

    private Stock findOrCreateStock(String ticker) {
        Stock stock = Stock.findById(ticker);
        if (stock == null) {
            stock = new Stock();
            stock.ticker = ticker;
            stock.persist();
        }
        return stock;
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
