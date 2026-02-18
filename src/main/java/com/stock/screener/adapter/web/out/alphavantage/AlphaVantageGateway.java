package com.stock.screener.adapter.web.out.alphavantage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.OverviewResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AlphaVantageGateway {

    private static final String FUNCTION_OVERVIEW = "OVERVIEW";
    private static final String FUNCTION_BALANCE_SHEET = "BALANCE_SHEET";
    private static final String FUNCTION_INCOME_STATEMENT = "INCOME_STATEMENT";

    private final AlphaVantageClient client;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    @Inject
    public AlphaVantageGateway(
            @RestClient AlphaVantageClient client,
            ObjectMapper objectMapper,
            @ConfigProperty(name = "alphavantage.api.key") String apiKey
    ) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    @Transactional
    public OverviewResponse fetchOverview(String ticker) {
        OverviewResponse response = client.getOverview(FUNCTION_OVERVIEW, ticker, apiKey);
        persistLog(ticker, FUNCTION_OVERVIEW, response);
        return response;
    }

    @Transactional
    public BalanceSheetResponse fetchBalanceSheet(String ticker) {
        BalanceSheetResponse response = client.getBalanceSheet(FUNCTION_BALANCE_SHEET, ticker, apiKey);
        persistLog(ticker, FUNCTION_BALANCE_SHEET, response);
        return response;
    }

    @Transactional
    public IncomeStatementResponse fetchIncomeStatement(String ticker) {
        IncomeStatementResponse response = client.getIncomeStatement(FUNCTION_INCOME_STATEMENT, ticker, apiKey);
        persistLog(ticker, FUNCTION_INCOME_STATEMENT, response);
        return response;
    }

    private void persistLog(String ticker, String functionName, Object response) {
        String rawJson = serializeToJson(response);
        var log = new AlphaVantageResponseLog(ticker, functionName, rawJson);
        log.persist();
    }

    private String serializeToJson(Object response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new AlphaVantageSerializationException(
                    "Failed to serialize Alpha Vantage response to JSON", e);
        }
    }
}
