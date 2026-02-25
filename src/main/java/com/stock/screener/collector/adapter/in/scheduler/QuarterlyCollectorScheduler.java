package com.stock.screener.collector.adapter.in.scheduler;

import com.stock.screener.collector.application.port.in.CollectQuarterlyDataUseCase;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
class QuarterlyCollectorScheduler {

    private final CollectQuarterlyDataUseCase collectQuarterlyDataUseCase;
    private final TickerReaderPort tickerReaderPort;

//    TODO: this should not run just every 3 months, but rather search for stocks with updatedAt > 3 months....
    @Scheduled(cron = "{collector.scheduler.quarterly.cron}", identity = "quarterly-collector")
    void run() {
        log.info("Quarterly collector scheduler triggered");

        tickerReaderPort.readTickers().forEach(ticker -> {
            try {
                collectQuarterlyDataUseCase.collectQuarterlyData(ticker);
            } catch (Exception ex) {
                log.error("Quarterly collection failed for ticker: {}", ticker, ex);
            }
        });

        log.info("Quarterly collector scheduler finished");
    }
}
