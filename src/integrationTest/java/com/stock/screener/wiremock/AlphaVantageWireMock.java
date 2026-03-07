package com.stock.screener.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Registration helper for AlphaVantage WireMock stubs.
 *
 * <p>Methods without a {@link WireMockServer} parameter resolve the server automatically via
 * {@link WireMockTestResource#getServer()} and are convenient for use in static
 * {@code @BeforeAll} methods. Methods that accept an explicit server are available as overloads
 * for tests that have a {@link WireMockServer} field injected by {@link WireMockTestResource}.
 */
public final class AlphaVantageWireMock {

    public static final String FUNCTION_OVERVIEW = "OVERVIEW";
    public static final String FUNCTION_BALANCE_SHEET = "BALANCE_SHEET";
    public static final String FUNCTION_INCOME_STATEMENT = "INCOME_STATEMENT";
    public static final String FUNCTION_CASH_FLOW = "CASH_FLOW";

    private static final String API_PATH = "/query";
    private static final String PARAM_FUNCTION = "function";
    private static final String PARAM_SYMBOL = "symbol";
    private static final String HEADER_API_KEY = "x-rapidapi-key";

    private static final String OVERVIEW_FILE = "overview.json";
    private static final String BALANCE_SHEET_FILE = "balance_sheet.json";
    private static final String INCOME_STATEMENT_FILE = "income_statement.json";
    private static final String CASH_FLOW_FILE = "cash_flow.json";

    private AlphaVantageWireMock() {
    }

    // ── No-arg server variants (use WireMockTestResource.getServer()) ──

    public static void stubOverview(String symbol) {
        registerStub(WireMockTestResource.getServer(), symbol, FUNCTION_OVERVIEW, OVERVIEW_FILE);
    }

    public static void stubBalanceSheet(String symbol) {
        registerStub(WireMockTestResource.getServer(), symbol, FUNCTION_BALANCE_SHEET, BALANCE_SHEET_FILE);
    }

    public static void stubIncomeStatement(String symbol) {
        registerStub(WireMockTestResource.getServer(), symbol, FUNCTION_INCOME_STATEMENT, INCOME_STATEMENT_FILE);
    }

    public static void stubCashFlow(String symbol) {
        registerStub(WireMockTestResource.getServer(), symbol, FUNCTION_CASH_FLOW, CASH_FLOW_FILE);
    }

    public static void stubAll(String symbol) {
        stubOverview(symbol);
        stubBalanceSheet(symbol);
        stubIncomeStatement(symbol);
        stubCashFlow(symbol);
    }

    public static void stubOverviewError(String symbol, int status) {
        registerErrorStub(WireMockTestResource.getServer(), symbol, FUNCTION_OVERVIEW, status);
    }

    public static void stubAllErrors(String symbol, int status) {
        WireMockServer server = WireMockTestResource.getServer();
        registerErrorStub(server, symbol, FUNCTION_OVERVIEW, status);
        registerErrorStub(server, symbol, FUNCTION_BALANCE_SHEET, status);
        registerErrorStub(server, symbol, FUNCTION_INCOME_STATEMENT, status);
        registerErrorStub(server, symbol, FUNCTION_CASH_FLOW, status);
    }

    // ── Explicit server overloads (for tests with injected WireMockServer field) ──

    public static void stubOverview(WireMockServer server, String symbol) {
        registerStub(server, symbol, FUNCTION_OVERVIEW, OVERVIEW_FILE);
    }

    public static void stubBalanceSheet(WireMockServer server, String symbol) {
        registerStub(server, symbol, FUNCTION_BALANCE_SHEET, BALANCE_SHEET_FILE);
    }

    public static void stubIncomeStatement(WireMockServer server, String symbol) {
        registerStub(server, symbol, FUNCTION_INCOME_STATEMENT, INCOME_STATEMENT_FILE);
    }

    public static void stubCashFlow(WireMockServer server, String symbol) {
        registerStub(server, symbol, FUNCTION_CASH_FLOW, CASH_FLOW_FILE);
    }

    public static void stubAll(WireMockServer server, String symbol) {
        stubOverview(server, symbol);
        stubBalanceSheet(server, symbol);
        stubIncomeStatement(server, symbol);
        stubCashFlow(server, symbol);
    }

    public static void stubOverviewError(WireMockServer server, String symbol, int status) {
        registerErrorStub(server, symbol, FUNCTION_OVERVIEW, status);
    }

    public static void stubAllErrors(WireMockServer server, String symbol, int status) {
        registerErrorStub(server, symbol, FUNCTION_OVERVIEW, status);
        registerErrorStub(server, symbol, FUNCTION_BALANCE_SHEET, status);
        registerErrorStub(server, symbol, FUNCTION_INCOME_STATEMENT, status);
        registerErrorStub(server, symbol, FUNCTION_CASH_FLOW, status);
    }

    // ── Private helpers ────────────────────────────────────────────────

    private static void registerStub(WireMockServer server, String symbol, String function, String stubFile) {
        server.stubFor(
                get(urlPathEqualTo(API_PATH))
                        .withQueryParam(PARAM_FUNCTION, equalTo(function))
                        .withQueryParam(PARAM_SYMBOL, equalTo(symbol))
                        .withHeader(HEADER_API_KEY, matching(".+"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(StubFileReader.read(stubFile))));
    }

    private static void registerErrorStub(WireMockServer server, String symbol, String function, int status) {
        server.stubFor(
                get(urlPathEqualTo(API_PATH))
                        .withQueryParam(PARAM_FUNCTION, equalTo(function))
                        .withQueryParam(PARAM_SYMBOL, equalTo(symbol))
                        .withHeader(HEADER_API_KEY, matching(".+"))
                        .willReturn(
                                aResponse()
                                        .withStatus(status)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("{\"error\":\"API error\"}")));
    }
}
