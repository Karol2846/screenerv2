package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.OverviewResponse;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
class AlphaVantageGateway implements AlphaVantageClient {

    private static final String OVERVIEW = "OVERVIEW";
    private static final String BALANCE_SHEET = "BALANCE_SHEET";
    private static final String INCOME_STATEMENT = "INCOME_STATEMENT";

    private final AlphaVantageApiClient client;
    private final ObjectMapper objectMapper;

    @Inject
    public AlphaVantageGateway(
            @RestClient AlphaVantageApiClient client,
            ObjectMapper objectMapper
    ) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RawOverview fetchOverview(String ticker) {
        OverviewResponse response = client.getOverview(OVERVIEW, ticker);
        persistLog(ticker, OVERVIEW, response);
        return AlphaVantageResponseMapper.toRawOverview(response);
    }

    @Transactional
    public RawBalanceSheet fetchBalanceSheet(String ticker) {
        BalanceSheetResponse response = client.getBalanceSheet(BALANCE_SHEET, ticker);
        persistLog(ticker, BALANCE_SHEET, response);
        return AlphaVantageResponseMapper.toRawBalanceSheet(response);
    }

    @Transactional
    public RawIncomeStatement fetchIncomeStatement(String ticker) {
        IncomeStatementResponse response = client.getIncomeStatement(INCOME_STATEMENT, ticker);
        persistLog(ticker, INCOME_STATEMENT, response);
        return AlphaVantageResponseMapper.toRawIncomeStatement(response);
    }

    private void persistLog(String ticker, String functionName, Object response) {
        try {
            String rawJson = objectMapper.writeValueAsString(response);
            var logEntry = new AlphaVantageResponseLog(ticker, functionName, rawJson);
            logEntry.persist();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Alpha Vantage response for ticker={}, function={}", ticker, functionName, e);
        }
    }
}
