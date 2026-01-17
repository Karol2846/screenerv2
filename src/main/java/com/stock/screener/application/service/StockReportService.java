package com.stock.screener.application.service;

import com.stock.screener.application.port.out.command.QuoteSummaryCommand;
import com.stock.screener.application.port.out.command.YahooFinanceClient;
import com.stock.screener.domain.entity.Stock;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.stock.screener.application.mapper.QuoteSummaryMapper.toMarketData;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StockReportService {

    private final YahooFinanceClient yahooClient;

    public void upsertYahooReport(String ticker) {
        QuoteSummaryCommand command = yahooClient.getQuoteSummary(ticker);
        Stock stock = Stock.findOrCreate(command.ticker());

        stock.updateMarketData(toMarketData(command));

        //TODO:
        // - update monthly report
        // - update quarterly report (remember to set integrityStatus=YH_PARTIAL)
        // - think about how to indicate any missing data...some additional status flag, or maybe it should be hendled only during analysis?
    }
}
