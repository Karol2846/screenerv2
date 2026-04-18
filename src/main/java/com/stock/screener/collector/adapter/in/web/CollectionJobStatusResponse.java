package com.stock.screener.collector.adapter.in.web;

import com.stock.screener.collector.application.port.in.CollectionJobStatus;

import java.time.Instant;

record CollectionJobStatusResponse(
        String jobId,
        String jobType,
        String target,
        String state,
        Integer totalTickers,
        int processedTickers,
        int successfulTickers,
        int failedTickers,
        String errorMessage,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt
) {

    static CollectionJobStatusResponse from(CollectionJobStatus status) {
        return new CollectionJobStatusResponse(
                status.jobId().toString(),
                status.jobType().name(),
                status.target(),
                status.state().name(),
                status.totalTickers(),
                status.processedTickers(),
                status.successfulTickers(),
                status.failedTickers(),
                status.errorMessage(),
                status.createdAt(),
                status.startedAt(),
                status.finishedAt()
        );
    }
}
