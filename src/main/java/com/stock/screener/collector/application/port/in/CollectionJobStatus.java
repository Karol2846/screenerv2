package com.stock.screener.collector.application.port.in;

import java.time.Instant;
import java.util.UUID;

public record CollectionJobStatus(
        UUID jobId,
        CollectionJobType jobType,
        String target,
        CollectionJobState state,
        Integer totalTickers,
        int processedTickers,
        int successfulTickers,
        int failedTickers,
        String errorMessage,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt
) {
}
