package com.stock.screener.collector.adapter.in.scheduler;

import com.stock.screener.collector.application.port.in.CollectDailyDataUseCase;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DailyCollectorScheduler {

    private final CollectDailyDataUseCase collectDailyDataUseCase;
    private final TickerReaderPort tickerReaderPort;

    @Scheduled(cron = "{collector.scheduler.daily.cron}", identity = "daily-collector")
    void run() {
        log.info("Daily collector scheduler triggered");

        tickerReaderPort.readTickers().forEach(ticker -> {
            try {
                collectDailyDataUseCase.collectDailyData(ticker);
            } catch (Exception ex) {
                log.error("Daily collection failed for ticker: {}", ticker, ex);
            }
        });

        log.info("Daily collector scheduler finished");
    }
}
