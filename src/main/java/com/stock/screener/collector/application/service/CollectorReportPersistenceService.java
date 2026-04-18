package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;
import com.stock.screener.collector.domain.entity.MonthlyReport;
import com.stock.screener.collector.domain.entity.QuarterlyReport;
import com.stock.screener.collector.domain.valueobject.snapshot.FinancialDataSnapshot;
import com.stock.screener.collector.domain.valueobject.snapshot.MarketDataSnapshot;
import com.stock.screener.common.Sector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;

@ApplicationScoped
class CollectorReportPersistenceService {

    @Transactional
    void upsertMonthlyReport(String ticker, RawOverview rawOverview, MarketDataSnapshot snapshot) {
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

    @Transactional
    void upsertQuarterlyReport(String ticker, RawBalanceSheet rawBalance, FinancialDataSnapshot financialSnapshot) {
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
