package com.stock.screener.collector.adapter.in.web;

import java.util.UUID;

record CollectionJobAcceptedResponse(String jobId, String statusUrl) {

    static CollectionJobAcceptedResponse from(UUID jobId) {
        return new CollectionJobAcceptedResponse(jobId.toString(), "/api/collector/jobs/" + jobId);
    }
}
