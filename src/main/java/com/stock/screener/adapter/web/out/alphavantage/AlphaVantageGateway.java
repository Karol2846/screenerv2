package com.stock.screener.adapter.web.out.alphavantage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.OverviewResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class AlphaVantageGateway {

    private static final String OVERVIEW = "OVERVIEW";
    private static final String BALANCE_SHEET = "BALANCE_SHEET";
    private static final String INCOME_STATEMENT = "INCOME_STATEMENT";

    private final AlphaVantageClient client;
    private final ObjectMapper objectMapper;

    @Inject
    public AlphaVantageGateway(
            @RestClient AlphaVantageClient client,
            ObjectMapper objectMapper
    ) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OverviewResponse fetchOverview(String ticker) {
        OverviewResponse response = client.getOverview(OVERVIEW, ticker);
        persistLog(ticker, OVERVIEW, response);
        return response;
    }

    @Transactional
    public BalanceSheetResponse fetchBalanceSheet(String ticker) {
        BalanceSheetResponse response = client.getBalanceSheet(BALANCE_SHEET, ticker);
        persistLog(ticker, BALANCE_SHEET, response);
        return response;
    }

    @Transactional
    public IncomeStatementResponse fetchIncomeStatement(String ticker) {
        IncomeStatementResponse response = client.getIncomeStatement(INCOME_STATEMENT, ticker);
        persistLog(ticker, INCOME_STATEMENT, response);
        return response;
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
