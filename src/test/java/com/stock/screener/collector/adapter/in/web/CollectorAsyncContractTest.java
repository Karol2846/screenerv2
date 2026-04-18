package com.stock.screener.collector.adapter.in.web;

import com.stock.screener.collector.application.port.in.CollectionJobState;
import com.stock.screener.collector.application.port.in.CollectionJobStatus;
import com.stock.screener.collector.application.port.in.CollectionJobType;
import com.stock.screener.collector.application.port.in.ManualCollectionJobUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@QuarkusTest
class CollectorAsyncContractTest {

    @InjectMock
    ManualCollectionJobUseCase manualCollectionJobUseCase;

    @Test
    void shouldReturn202ForMonthlyAndQuarterlyManualTriggers() {
        var monthlyTickerJobId = UUID.randomUUID();
        var monthlyAllJobId = UUID.randomUUID();
        var quarterlyTickerJobId = UUID.randomUUID();
        var quarterlyAllJobId = UUID.randomUUID();

        when(manualCollectionJobUseCase.startMonthlyTickerJob("META")).thenReturn(monthlyTickerJobId);
        when(manualCollectionJobUseCase.startMonthlyAllTickersJob()).thenReturn(monthlyAllJobId);
        when(manualCollectionJobUseCase.startQuarterlyTickerJob("META")).thenReturn(quarterlyTickerJobId);
        when(manualCollectionJobUseCase.startQuarterlyAllTickersJob()).thenReturn(quarterlyAllJobId);

        given()
                .post("/api/collector/monthly/{ticker}", "META")
                .then()
                .statusCode(202)
                .body("jobId", equalTo(monthlyTickerJobId.toString()))
                .body("statusUrl", equalTo("/api/collector/jobs/" + monthlyTickerJobId));

        given()
                .post("/api/collector/monthly/all")
                .then()
                .statusCode(202)
                .body("jobId", equalTo(monthlyAllJobId.toString()));

        given()
                .post("/api/collector/quarterly/{ticker}", "META")
                .then()
                .statusCode(202)
                .body("jobId", equalTo(quarterlyTickerJobId.toString()))
                .body("statusUrl", equalTo("/api/collector/jobs/" + quarterlyTickerJobId));

        given()
                .post("/api/collector/quarterly/all")
                .then()
                .statusCode(202)
                .body("jobId", equalTo(quarterlyAllJobId.toString()));
    }

    @Test
    void shouldReturnJobStatus() {
        var jobId = UUID.randomUUID();
        var now = Instant.now();
        var status = new CollectionJobStatus(
                jobId,
                CollectionJobType.MONTHLY_SINGLE_TICKER,
                "META",
                CollectionJobState.RUNNING,
                1,
                0,
                0,
                0,
                null,
                now,
                now,
                null
        );

        when(manualCollectionJobUseCase.getJobStatus(jobId)).thenReturn(Optional.of(status));

        given()
                .get("/api/collector/jobs/{jobId}", jobId)
                .then()
                .statusCode(200)
                .body("jobId", equalTo(jobId.toString()))
                .body("state", equalTo("RUNNING"))
                .body("jobType", equalTo("MONTHLY_SINGLE_TICKER"))
                .body("target", equalTo("META"));
    }

    @Test
    void shouldReturn404WhenJobStatusNotFound() {
        var missingJobId = UUID.randomUUID();
        when(manualCollectionJobUseCase.getJobStatus(missingJobId)).thenReturn(Optional.empty());

        given()
                .get("/api/collector/jobs/{jobId}", missingJobId)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn400ForInvalidJobId() {
        given()
                .get("/api/collector/jobs/{jobId}", "not-a-uuid")
                .then()
                .statusCode(400)
                .body(equalTo("Invalid jobId format"));
    }
}
