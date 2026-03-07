package com.stock.screener.collector;

import com.stock.screener.collector.domain.entity.MonthlyReport;
import com.stock.screener.wiremock.WireMockTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(WireMockTestResource.class)
class MonthlyReportCollectionIT {

    private static final String TICKER = "META";

    @Inject
    EntityManager entityManager;

    @Test
    void shouldCollectMonthlyDataAndPersistRecords() {
        given()
                .post("/api/collector/monthly/{ticker}", TICKER)
                .then()
                .statusCode(200);

        Long reportCount = MonthlyReport.count("ticker", TICKER);
        assertThat(reportCount).isEqualTo(1);

        Long alphaVantageLogCount = ((Number) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM alpha_vantage_response_log WHERE ticker = :ticker")
                .setParameter("ticker", TICKER)
                .getSingleResult()).longValue();
        assertThat(alphaVantageLogCount).isGreaterThanOrEqualTo(1);

        Long yhFinanceLogCount = ((Number) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM yh_finance_response_log WHERE ticker = :ticker")
                .setParameter("ticker", TICKER)
                .getSingleResult()).longValue();
        assertThat(yhFinanceLogCount).isGreaterThanOrEqualTo(1);
    }
}
