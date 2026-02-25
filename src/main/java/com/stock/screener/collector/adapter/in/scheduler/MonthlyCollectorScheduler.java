package com.stock.screener.collector.adapter.in.scheduler;

import com.stock.screener.collector.application.port.in.CollectMonthlyDataUseCase;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
class MonthlyCollectorScheduler {

    private final CollectMonthlyDataUseCase collectMonthlyDataUseCase;
    private final TickerReaderPort tickerReaderPort;

    @Scheduled(cron = "{collector.scheduler.monthly.cron}", identity = "monthly-collector")
    void run() {
        log.info("Monthly collector scheduler triggered");

        tickerReaderPort.readTickers().forEach(ticker -> {
            try {
                collectMonthlyDataUseCase.collectMonthlyData(ticker);
            } catch (Exception ex) {
                log.error("Monthly collection failed for ticker: {}", ticker, ex);
            }
        });

        log.info("Monthly collector scheduler finished");
    }
}
