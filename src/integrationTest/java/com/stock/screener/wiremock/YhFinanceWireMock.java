package com.stock.screener.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Registration helper for YhFinance WireMock stubs.
 *
 * <p>Methods without a {@link WireMockServer} parameter resolve the server automatically via
 * {@link WireMockTestResource#getServer()} and are convenient for use in static
 * {@code @BeforeAll} methods. Methods that accept an explicit server are available as overloads
 * for tests that have a {@link WireMockServer} field injected by {@link WireMockTestResource}.
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

    // ── No-arg server variants (use WireMockTestResource.getServer()) ──

    public static void stubQuoteSummary(String symbol) {
        stubQuoteSummary(WireMockTestResource.getServer(), symbol);
    }

    public static void stubQuoteSummaryError(String symbol, int status) {
        stubQuoteSummaryError(WireMockTestResource.getServer(), symbol, status);
    }

    // ── Explicit server overloads (for tests with injected WireMockServer field) ──

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

    public static void stubQuoteSummaryError(WireMockServer server, String symbol, int status) {
        String path = String.format(API_PATH_TEMPLATE, symbol);

        server.stubFor(
                get(urlPathEqualTo(path))
                        .withQueryParam(PARAM_MODULES, equalTo(DEFAULT_MODULES))
                        .withQueryParam(PARAM_LANG, equalTo(DEFAULT_LANG))
                        .withQueryParam(PARAM_REGION, equalTo(DEFAULT_REGION))
                        .withHeader(HEADER_API_KEY, matching(".+"))
                        .willReturn(
                                aResponse()
                                        .withStatus(status)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("{\"error\":\"API error\"}")));
    }
}
