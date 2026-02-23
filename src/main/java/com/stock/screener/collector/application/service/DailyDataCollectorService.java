package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.mapper.StockDataMapper;
import com.stock.screener.collector.application.port.in.CollectDailyDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import com.stock.screener.domain.entity.Stock;
import com.stock.screener.domain.valueobject.Sector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DailyDataCollectorService implements CollectDailyDataUseCase {

    private final AlphaVantageClient alphaVantageClient;
    private final YahooFinanceClient yahooFinanceClient;
    private final StockDataMapper stockDataMapper;

    @Override
    @Transactional
    public void collectDailyData(String ticker) {
        log.info("Daily collection for: {}", ticker);

        Stock stock = findOrCreateStock(ticker);

        var rawOverview = alphaVantageClient.fetchOverview(ticker);
        var yhResponse = yahooFinanceClient.getQuoteSummary(ticker);

        if (rawOverview != null && rawOverview.sector() != null) {
            stock.sector = Sector.fromString(rawOverview.sector());
        }

        //TODO: this is fking nonsens. Just map overview to marketData.
        //   Rename MarketDataSnapshoot cause agent interpret it wrongly
        var snapshot = stockDataMapper.toMarketDataSnapshot(rawOverview, yhResponse);
        stock.marketData = stockDataMapper.toMarketData(snapshot);
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
