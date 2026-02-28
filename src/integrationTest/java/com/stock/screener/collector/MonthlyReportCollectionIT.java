package com.stock.screener.collector;

import com.stock.screener.collector.domain.entity.MonthlyReport;
import com.stock.screener.wiremock.AlphaVantageWireMock;
import com.stock.screener.wiremock.WireMockServerConfig;
import com.stock.screener.wiremock.YhFinanceWireMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(IntegrationTestProfile.class)
@ExtendWith(WireMockServerConfig.class)
class MonthlyReportCollectionIT {

    private static final String TICKER = "META";

    @Inject
    EntityManager entityManager;

    @BeforeAll
    static void stubExternalApis() {
        AlphaVantageWireMock.stubOverview(TICKER);
        YhFinanceWireMock.stubQuoteSummary(TICKER);
    }

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
