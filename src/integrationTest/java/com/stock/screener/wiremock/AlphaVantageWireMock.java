package com.stock.screener.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Registration helper for AlphaVantage WireMock stubs.
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

    public static void stubOverview(String symbol) {
        registerStub(symbol, FUNCTION_OVERVIEW, OVERVIEW_FILE);
    }

    public static void stubBalanceSheet(String symbol) {
        registerStub(symbol, FUNCTION_BALANCE_SHEET, BALANCE_SHEET_FILE);
    }

    public static void stubIncomeStatement(String symbol) {
        registerStub(symbol, FUNCTION_INCOME_STATEMENT, INCOME_STATEMENT_FILE);
    }

    public static void stubCashFlow(String symbol) {
        registerStub(symbol, FUNCTION_CASH_FLOW, CASH_FLOW_FILE);
    }

    public static void stubAll(String symbol) {
        stubOverview(symbol);
        stubBalanceSheet(symbol);
        stubIncomeStatement(symbol);
        stubCashFlow(symbol);
    }

    public static void stubOverviewError(String symbol, int status) {
        registerErrorStub(symbol, FUNCTION_OVERVIEW, status);
    }

    public static void stubAllErrors(String symbol, int status) {
        registerErrorStub(symbol, FUNCTION_OVERVIEW, status);
        registerErrorStub(symbol, FUNCTION_BALANCE_SHEET, status);
        registerErrorStub(symbol, FUNCTION_INCOME_STATEMENT, status);
        registerErrorStub(symbol, FUNCTION_CASH_FLOW, status);
    }

    /**
     * Stubs an Alpha Vantage rate-limit response: HTTP 200 with an "Information" body.
     * This is how AV signals a daily quota exhaustion — not via HTTP 4xx but via a 200 JSON payload.
     */
    public static void stubOverviewRateLimit(String symbol) {
        registerRateLimitStub(symbol, FUNCTION_OVERVIEW);
    }

    public static void stubAllRateLimits(String symbol) {
        registerRateLimitStub(symbol, FUNCTION_OVERVIEW);
        registerRateLimitStub(symbol, FUNCTION_BALANCE_SHEET);
        registerRateLimitStub(symbol, FUNCTION_INCOME_STATEMENT);
        registerRateLimitStub(symbol, FUNCTION_CASH_FLOW);
    }

    private static void registerErrorStub(String symbol, String function, int status) {
        WireMockServer server = WireMockServerConfig.getServer();

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

    private static void registerRateLimitStub(String symbol, String function) {
        WireMockServer server = WireMockServerConfig.getServer();

        server.stubFor(
                get(urlPathEqualTo(API_PATH))
                        .withQueryParam(PARAM_FUNCTION, equalTo(function))
                        .withQueryParam(PARAM_SYMBOL, equalTo(symbol))
                        .withHeader(HEADER_API_KEY, matching(".+"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("{\"Information\": \"Thank you for using Alpha Vantage! Our standard API call frequency is 25 requests per day. Please subscribe to a premium plan.\"}")));
    }

    private static void registerStub(String symbol, String function, String stubFile) {
        WireMockServer server = WireMockServerConfig.getServer();

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
}
