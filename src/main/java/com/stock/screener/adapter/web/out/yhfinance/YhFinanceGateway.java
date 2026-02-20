package com.stock.screener.adapter.web.out.yhfinance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.screener.adapter.web.out.yhfinance.exception.ClientException;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResponse;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResult;
import com.stock.screener.application.port.out.yhfinance.command.QuoteSummaryCommand;
import com.stock.screener.application.port.out.yhfinance.YahooFinanceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jspecify.annotations.NonNull;
import java.util.List;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientMapper.toCommand;

@Slf4j
@ApplicationScoped
class YhFinanceGateway implements YahooFinanceClient {

    private final YhFinanceApiClient apiClient;
    private final ObjectMapper objectMapper;

    private static final String DEFAULT_MODULES = "earningsTrend,recommendationTrend";
    private static final String DEFAULT_LANG = "en";
    private static final String DEFAULT_REGION = "US";

    @Inject
    public YhFinanceGateway(@RestClient YhFinanceApiClient apiClient, YhFinanceClientMapper mapper, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public QuoteSummaryCommand getQuoteSummary(@NonNull String ticker) {
        QuoteSummaryResponse response = apiClient.getQuoteSummary(ticker, DEFAULT_MODULES, DEFAULT_LANG, DEFAULT_REGION);
        validateClientResponse(ticker, response);
        persistLog(ticker, response);
        List<QuoteSummaryResult> results = response.quoteSummary().result();
        return toCommand(ticker, results.getFirst());
    }

    private static void validateClientResponse(String symbol, QuoteSummaryResponse response) {
        if (response == null || response.quoteSummary() == null) {
            throw new ClientException("Null response from YH Finance API for symbol: %s, api repose: ", symbol, response);
        }
        else if(response.quoteSummary().error() != null) {
            throw new ClientException("Error fetching data for symbol: %s, error: %s", symbol, response.quoteSummary().error());
        }

        var results = response.quoteSummary().result();
        if (results == null || results.isEmpty()) {
            throw new ClientException("Null response from YH Finance API for symbol: %s, api response: %s", symbol, response);
        }
    }

    private void persistLog(String ticker, Object response) {
        try {
            String rawJson = objectMapper.writeValueAsString(response);
            new YhFinanceResponseLog(ticker, rawJson).persist();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize YH Finance response for ticker={}, function={}", ticker, YhFinanceGateway.DEFAULT_MODULES, e);
        }
    }
}

