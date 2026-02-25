package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.mapper.StockDataMapper;
import com.stock.screener.collector.application.port.in.CollectMonthlyDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import com.stock.screener.domain.entity.MonthlyReport;
import com.stock.screener.domain.valueobject.Sector;
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

        var rawOverview = alphaVantageClient.fetchOverview(ticker);
        var rawIncome = alphaVantageClient.fetchIncomeStatement(ticker);
        var yhResponse = yahooFinanceClient.getQuoteSummary(ticker);

        var snapshot = stockDataMapper.toMarketDataSnapshot(rawOverview, yhResponse, rawIncome);

        MonthlyReport report = MonthlyReport.find("ticker", ticker).firstResult();
        if (report == null) {
            report = new MonthlyReport();
            report.ticker = ticker;
        }

        if (rawOverview != null && rawOverview.sector() != null) {
            report.sector = Sector.fromString(rawOverview.sector());
        }

        report.updateMetrics(snapshot);
        report.persist();
    }
}
