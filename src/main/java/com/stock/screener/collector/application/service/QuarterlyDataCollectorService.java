package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.mapper.StockDataMapper;
import com.stock.screener.collector.application.port.in.CollectQuarterlyDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.domain.entity.MonthlyReport;
import com.stock.screener.domain.entity.QuarterlyReport;
import com.stock.screener.domain.valueobject.Sector;
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

        var rawBalance = alphaVantageClient.fetchBalanceSheet(ticker);
        var rawIncome = alphaVantageClient.fetchIncomeStatement(ticker);
        var rawCash = alphaVantageClient.fetchCashFlow(ticker);

        var financialSnapshot = stockDataMapper.toFinancialDataSnapshot(rawBalance, rawIncome, rawCash);

        Sector sector = resolveSector(ticker);

        QuarterlyReport qReport = QuarterlyReport.find("ticker", ticker).firstResult();
        if (qReport == null) {
            qReport = new QuarterlyReport();
            qReport.ticker = ticker;
            qReport.fiscalDateEnding = resolveFiscalDate(rawBalance);
        }

        qReport.sector = sector;
        qReport.updateMetrics(financialSnapshot, sector);
        qReport.persist();
    }

    private Sector resolveSector(String ticker) {
        MonthlyReport monthly = MonthlyReport.find("ticker", ticker).firstResult();
        return monthly != null && monthly.sector != null ? monthly.sector : Sector.OTHER;
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
