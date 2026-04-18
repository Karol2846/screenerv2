package com.stock.screener.collector.application.port.in;

import java.util.Optional;
import java.util.UUID;

public interface ManualCollectionJobUseCase {
    UUID startMonthlyTickerJob(String ticker);

    UUID startMonthlyAllTickersJob();

    UUID startQuarterlyTickerJob(String ticker);

    UUID startQuarterlyAllTickersJob();

    Optional<CollectionJobStatus> getJobStatus(UUID jobId);
}
