package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.port.in.*;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
class ManualCollectionJobService implements ManualCollectionJobUseCase {

    private final CollectMonthlyDataUseCase collectMonthlyDataUseCase;
    private final CollectQuarterlyDataUseCase collectQuarterlyDataUseCase;
    private final TickerReaderPort tickerReaderPort;

    private final ExecutorService jobExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ConcurrentMap<UUID, JobExecutionState> jobs = new ConcurrentHashMap<>();

    @Override
    public UUID startMonthlyTickerJob(String ticker) {
        var normalizedTicker = ticker.toUpperCase();
        var jobId = registerJob(CollectionJobType.MONTHLY_SINGLE_TICKER, normalizedTicker, 1);
        jobExecutor.execute(() -> runSingleTickerJob(jobId, normalizedTicker, collectMonthlyDataUseCase::collectMonthlyData));
        return jobId;
    }

    @Override
    public UUID startMonthlyAllTickersJob() {
        var jobId = registerJob(CollectionJobType.MONTHLY_ALL_TICKERS, "ALL", null);
        jobExecutor.execute(() -> runAllTickersJob(jobId, collectMonthlyDataUseCase::collectMonthlyData));
        return jobId;
    }

    @Override
    public UUID startQuarterlyTickerJob(String ticker) {
        var normalizedTicker = ticker.toUpperCase();
        var jobId = registerJob(CollectionJobType.QUARTERLY_SINGLE_TICKER, normalizedTicker, 1);
        jobExecutor.execute(() -> runSingleTickerJob(jobId, normalizedTicker, collectQuarterlyDataUseCase::collectQuarterlyData));
        return jobId;
    }

    @Override
    public UUID startQuarterlyAllTickersJob() {
        var jobId = registerJob(CollectionJobType.QUARTERLY_ALL_TICKERS, "ALL", null);
        jobExecutor.execute(() -> runAllTickersJob(jobId, collectQuarterlyDataUseCase::collectQuarterlyData));
        return jobId;
    }

    @Override
    public Optional<CollectionJobStatus> getJobStatus(UUID jobId) {
        return Optional.ofNullable(jobs.get(jobId))
                .map(JobExecutionState::toStatus);
    }

    @PreDestroy
    void shutdownExecutor() {
        jobExecutor.shutdown();
    }

    private UUID registerJob(CollectionJobType jobType, String target, Integer totalTickers) {
        var jobId = UUID.randomUUID();
        jobs.put(jobId, JobExecutionState.queued(jobId, jobType, target, totalTickers));
        return jobId;
    }

    private void runSingleTickerJob(UUID jobId, String ticker, Consumer<String> collector) {
        var job = jobs.get(jobId);
        job.markRunning();
        try {
            collector.accept(ticker);
            job.markTickerSuccess();
            job.markCompleted();
        } catch (Exception ex) {
            log.error("Async {} job failed for ticker: {}", job.jobType, ticker, ex);
            job.markTickerFailure();
            job.markFailed(ex.getMessage());
        }
    }

    private void runAllTickersJob(UUID jobId, Consumer<String> collector) {
        var job = jobs.get(jobId);
        job.markRunning();
        try {
            var tickers = tickerReaderPort.readTickers();
            job.setTotalTickers(tickers.size());
            for (String ticker : tickers) {
                try {
                    collector.accept(ticker);
                    job.markTickerSuccess();
                } catch (Exception ex) {
                    log.error("Async {} job failed for ticker: {}", job.jobType, ticker, ex);
                    job.markTickerFailure();
                }
            }
            job.markCompleted();
        } catch (Exception ex) {
            log.error("Async {} job failed while preparing tickers", job.jobType, ex);
            job.markFailed(ex.getMessage());
        }
    }

    private static final class JobExecutionState {

        private final UUID jobId;
        private final CollectionJobType jobType;
        private final String target;
        private final Instant createdAt;
        private final AtomicInteger processedTickers = new AtomicInteger();
        private final AtomicInteger successfulTickers = new AtomicInteger();
        private final AtomicInteger failedTickers = new AtomicInteger();

        private volatile CollectionJobState state;
        private volatile Integer totalTickers;
        private volatile String errorMessage;
        private volatile Instant startedAt;
        private volatile Instant finishedAt;

        private JobExecutionState(UUID jobId, CollectionJobType jobType, String target, Integer totalTickers) {
            this.jobId = jobId;
            this.jobType = jobType;
            this.target = target;
            this.totalTickers = totalTickers;
            this.createdAt = Instant.now();
            this.state = CollectionJobState.QUEUED;
        }

        private static JobExecutionState queued(UUID jobId, CollectionJobType jobType, String target, Integer totalTickers) {
            return new JobExecutionState(jobId, jobType, target, totalTickers);
        }

        private void markRunning() {
            state = CollectionJobState.RUNNING;
            startedAt = Instant.now();
        }

        private void setTotalTickers(int totalTickers) {
            this.totalTickers = totalTickers;
        }

        private void markTickerSuccess() {
            processedTickers.incrementAndGet();
            successfulTickers.incrementAndGet();
        }

        private void markTickerFailure() {
            processedTickers.incrementAndGet();
            failedTickers.incrementAndGet();
        }

        private void markCompleted() {
            state = CollectionJobState.COMPLETED;
            finishedAt = Instant.now();
        }

        private void markFailed(String errorMessage) {
            state = CollectionJobState.FAILED;
            this.errorMessage = errorMessage;
            finishedAt = Instant.now();
        }

        private CollectionJobStatus toStatus() {
            return new CollectionJobStatus(
                    jobId,
                    jobType,
                    target,
                    state,
                    totalTickers,
                    processedTickers.get(),
                    successfulTickers.get(),
                    failedTickers.get(),
                    errorMessage,
                    createdAt,
                    startedAt,
                    finishedAt
            );
        }
    }
}
