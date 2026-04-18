package com.stock.screener.collector;

import com.stock.screener.collector.domain.entity.MonthlyReport;
import com.stock.screener.collector.domain.valueobject.ReportIntegrityStatus;
import com.stock.screener.common.Sector;
import com.stock.screener.wiremock.AlphaVantageWireMock;
import com.stock.screener.wiremock.WireMockServerConfig;
import com.stock.screener.wiremock.YhFinanceWireMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static com.stock.screener.assertions.ResponseLogAssertions.assertResponseLogs;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(IntegrationTestProfile.class)
@ExtendWith(WireMockServerConfig.class)
@DisplayName("Monthly Report Collection — Integration Tests")
class MonthlyReportCollectionIT {

    private static final String TICKER = "META";

    // TODO: Add equivalent async + resilience integration scenarios for quarterly collection endpoints.
    // TODO: Extend tests with job polling edge-cases (timeout/cancel/interrupted polling) for collector review pass.
    @Inject
    EntityManager entityManager;

    @BeforeAll
    static void stubExternalApis() {
        AlphaVantageWireMock.stubOverview(TICKER);
        YhFinanceWireMock.stubQuoteSummary(TICKER);
    }

    // ── Happy Path ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Happy Path")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class HappyPath {

        @Test
        @Order(1)
        @DisplayName("POST /monthly/{ticker} returns 200 and persists exactly one report")
        void shouldCollectAndPersistOneReport() {
            given()
                    .post("/api/collector/monthly/{ticker}", TICKER)
                    .then()
                    .statusCode(200);

            assertThat(MonthlyReport.count("ticker", TICKER)).isEqualTo(1);
        }

        @Test
        @Order(2)
        @DisplayName("Persisted report has correct sector (from overview)")
        void shouldPersistCorrectSector() {
            MonthlyReport report = MonthlyReport.find("ticker", TICKER).firstResult();

            // Overview stub has "COMMUNICATION SERVICES" -> mapped to COMMUNICATION_SERVICES
            assertThat(report).isNotNull();
            assertThat(report.sector).isEqualTo(Sector.COMMUNICATION_SERVICES);
        }

        @Test
        @Order(3)
        @DisplayName("Persisted report has expected raw metric values from stubs")
        void shouldPersistExpectedMetricValues() {
            MonthlyReport report = MonthlyReport.find("ticker", TICKER).firstResult();
            assertThat(report).isNotNull();

            // From overview stub: ForwardPE = 21.28, AnalystTargetPrice = 861.42
            assertThat(report.forwardPeRatio).isEqualByComparingTo(new BigDecimal("21.28"));
            assertThat(report.targetPrice).isEqualByComparingTo(new BigDecimal("861.42"));

            // From YhFinance stub (period "0y"): forwardEpsGrowth & forwardRevenueGrowth present
            assertThat(report.forwardEpsGrowth).isNotNull();
            assertThat(report.forwardRevenueGrowth).isNotNull();
        }

        @Test
        @Order(4)
        @DisplayName("Persisted report has analyst ratings from YhFinance stub")
        void shouldPersistAnalystRatings() {
            MonthlyReport report = MonthlyReport.find("ticker", TICKER).firstResult();
            assertThat(report).isNotNull();
            assertThat(report.analystRatings).isNotNull();

            // From YhFinance stub: strongBuy=11, buy=51, hold=5, sell=0, strongSell=0
            assertThat(report.analystRatings.strongBuy()).isEqualTo(11);
            assertThat(report.analystRatings.buy()).isEqualTo(51);
            assertThat(report.analystRatings.hold()).isEqualTo(5);
            assertThat(report.analystRatings.sell()).isEqualTo(0);
            assertThat(report.analystRatings.strongSell()).isEqualTo(0);
        }

        @Test
        @Order(5)
        @DisplayName("Persisted report has READY_FOR_ANALYSIS integrity status")
        void shouldHaveReadyForAnalysisStatus() {
            MonthlyReport report = MonthlyReport.find("ticker", TICKER).firstResult();
            assertThat(report).isNotNull();
            assertThat(report.integrityStatus).isEqualTo(ReportIntegrityStatus.READY_FOR_ANALYSIS);
        }

        @Test
        @Order(6)
        @DisplayName("Computed value objects (psRatio, upsidePotential, forwardPeg) are populated")
        void shouldComputeValueObjects() {
            MonthlyReport report = MonthlyReport.find("ticker", TICKER).firstResult();
            assertThat(report).isNotNull();
            assertThat(report.psRatio).isNotNull();
            assertThat(report.upsidePotential).isNotNull();
            assertThat(report.forwardPegRatio).isNotNull();
        }

        @Test
        @Order(7)
        @DisplayName("No calculation errors for complete stub data")
        void shouldHaveNoCalculationErrors() {
            MonthlyReport report = MonthlyReport.find("ticker", TICKER).firstResult();
            assertThat(report).isNotNull();
            assertThat(report.calculationErrors).isEmpty();
        }

        @Test
        @Order(8)
        @DisplayName("Forecast date is populated on persist")
        void shouldHaveForecastDate() {
            MonthlyReport report = MonthlyReport.find("ticker", TICKER).firstResult();
            assertThat(report).isNotNull();
            assertThat(report.forecastDate).isNotNull();
        }
    }

    // ── API Response Logs ──────────────────────────────────────────────

    @Nested
    @DisplayName("API Response Logging")
    class ApiResponseLogging {

        @Test
        @DisplayName("AlphaVantage overview log is persisted for the ticker")
        void shouldPersistAlphaVantageOverviewLog() {
            given()
                    .post("/api/collector/monthly/{ticker}", TICKER)
                    .then()
                    .statusCode(200);

            assertResponseLogs(entityManager)
                    .hasAlphaVantageLogsForTicker(TICKER, 1)
                    .hasAlphaVantageLogForFunction(TICKER, "OVERVIEW");
        }

        @Test
        @DisplayName("YhFinance response log is persisted for the ticker")
        void shouldPersistYhFinanceLog() {
            given()
                    .post("/api/collector/monthly/{ticker}", TICKER)
                    .then()
                    .statusCode(200);

            assertResponseLogs(entityManager)
                    .hasYhFinanceLogsForTicker(TICKER, 1);
        }
    }

    @Nested
    @DisplayName("Idempotency")
    class Idempotency {

        @Test
        @DisplayName("Calling endpoint twice for the same ticker updates (not duplicates) the report")
        void shouldNotDuplicateReportOnSecondCall() {
            given()
                    .post("/api/collector/monthly/{ticker}", TICKER)
                    .then()
                    .statusCode(200);

            given()
                    .post("/api/collector/monthly/{ticker}", TICKER)
                    .then()
                    .statusCode(200);

            assertThat(MonthlyReport.count("ticker", TICKER)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Error Scenarios")
    class ErrorScenarios {

        private static final String AV_ERROR_TICKER = "AVFAIL";
        private static final String YH_ERROR_TICKER = "YHFAIL";
        private static final String BOTH_ERROR_TICKER = "BOTHFAIL";

        @BeforeAll
        static void stubErrors() {
            AlphaVantageWireMock.stubOverviewError(AV_ERROR_TICKER, 500);
            YhFinanceWireMock.stubQuoteSummary(AV_ERROR_TICKER);

            AlphaVantageWireMock.stubOverview(YH_ERROR_TICKER);
            YhFinanceWireMock.stubQuoteSummaryError(YH_ERROR_TICKER, 500);

            AlphaVantageWireMock.stubOverviewError(BOTH_ERROR_TICKER, 500);
            YhFinanceWireMock.stubQuoteSummaryError(BOTH_ERROR_TICKER, 500);
        }

        @Test
        @DisplayName("Returns 500 with 'external api throw unknown error' when AlphaVantage fails")
        void shouldReturn500WithErrorMessageWhenAlphaVantageFails() {
            String body = given()
                    .post("/api/collector/monthly/{ticker}", AV_ERROR_TICKER)
                    .then()
                    .statusCode(500)
                    .extract().body().asString();

            assertThat(body).isEqualTo("External api throw unknown error");
        }

        @Test
        @DisplayName("Returns 500 with 'External api throw unknown error' when YhFinance fails")
        void shouldReturn500WithErrorMessageWhenYhFinanceFails() {
            String body = given()
                    .post("/api/collector/monthly/{ticker}", YH_ERROR_TICKER)
                    .then()
                    .statusCode(500)
                    .extract().body().asString();

            assertThat(body).isEqualTo("External api throw unknown error");
        }

        @Test
        @DisplayName("Returns 500 with 'External api throw unknown error' when both APIs fail")
        void shouldReturn500WhenBothApisFail() {
            String body = given()
                    .post("/api/collector/monthly/{ticker}", BOTH_ERROR_TICKER)
                    .then()
                    .statusCode(500)
                    .extract().body().asString();

            assertThat(body).isEqualTo("External api throw unknown error");
        }

        @Test
        @DisplayName("No report persisted when AlphaVantage fails")
        void shouldNotPersistReportWhenAlphaVantageFails() {
            given()
                    .post("/api/collector/monthly/{ticker}", AV_ERROR_TICKER)
                    .then();

            assertThat(MonthlyReport.count("ticker", AV_ERROR_TICKER)).isZero();
        }

        @Test
        @DisplayName("No report persisted when YhFinance fails")
        void shouldNotPersistReportWhenYhFinanceFails() {
            given()
                    .post("/api/collector/monthly/{ticker}", YH_ERROR_TICKER)
                    .then();

            assertThat(MonthlyReport.count("ticker", YH_ERROR_TICKER)).isZero();
        }

        @Test
        @DisplayName("No report persisted when both APIs fail")
        void shouldNotPersistReportWhenBothFail() {
            given()
                    .post("/api/collector/monthly/{ticker}", BOTH_ERROR_TICKER)
                    .then();

            assertThat(MonthlyReport.count("ticker", BOTH_ERROR_TICKER)).isZero();
        }
    }
}
