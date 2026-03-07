package com.stock.screener.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Registration helper for YhFinance WireMock stubs.
 *
 * <p>Obtain the {@link WireMockServer} instance from the injected field in your test class:
 * <pre>{@code
 * @QuarkusTest
 * @QuarkusTestResource(WireMockTestResource.class)
 * class MyIT {
 *     WireMockServer wireMock;
 *
 *     @BeforeEach
 *     void setup() {
 *         YhFinanceWireMock.stubQuoteSummary(wireMock, "AAPL");
 *     }
 * }
 * }</pre>
 */
public final class YhFinanceWireMock {

    private static final String DEFAULT_MODULES = "earningsTrend,recommendationTrend,price";
    private static final String DEFAULT_LANG = "en";
    private static final String DEFAULT_REGION = "US";

    private static final String API_PATH_TEMPLATE = "/v11/finance/quoteSummary/%s";
    private static final String PARAM_MODULES = "modules";
    private static final String PARAM_LANG = "lang";
    private static final String PARAM_REGION = "region";
    private static final String HEADER_API_KEY = "X-API-KEY";

    private static final String STUB_FILE = "yh_finance_response.json";

    private YhFinanceWireMock() {
    }

    public static void stubQuoteSummary(WireMockServer server, String symbol) {
        String path = String.format(API_PATH_TEMPLATE, symbol);

        server.stubFor(
                get(urlPathEqualTo(path))
                        .withQueryParam(PARAM_MODULES, equalTo(DEFAULT_MODULES))
                        .withQueryParam(PARAM_LANG, equalTo(DEFAULT_LANG))
                        .withQueryParam(PARAM_REGION, equalTo(DEFAULT_REGION))
                        .withHeader(HEADER_API_KEY, matching(".+"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(StubFileReader.read(STUB_FILE))));
    }
}
