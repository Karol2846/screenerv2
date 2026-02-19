package com.stock.screener.adapter.web.out.yhfinance;

import com.stock.screener.adapter.web.out.yhfinance.exception.ClientException;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResponse;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResult;
import com.stock.screener.application.port.out.yhfinance.command.QuoteSummaryCommand;
import com.stock.screener.application.port.out.yhfinance.YahooFinanceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jspecify.annotations.NonNull;
import java.util.List;

@ApplicationScoped
public class YhFinanceService implements YahooFinanceClient {

    private final YhFinanceApiClient apiClient;
    private final YhFinanceClientMapper mapper;

    String DEFAULT_MODULES = "earningsTrend,recommendationTrend,financialData,assetProfile,summaryDetail";
    String DEFAULT_LANG = "en";
    String DEFAULT_REGION = "US";

    @Inject
    public YhFinanceService(@RestClient YhFinanceApiClient apiClient, YhFinanceClientMapper mapper) {
        this.apiClient = apiClient;
        this.mapper = mapper;
    }

    @Override
    public QuoteSummaryCommand getQuoteSummary(@NonNull String ticker) {
        QuoteSummaryResponse response = apiClient.getQuoteSummary(ticker, DEFAULT_MODULES, DEFAULT_LANG, DEFAULT_REGION);
        validateClientResponse(ticker, response);

        List<QuoteSummaryResult> results = response.quoteSummary().result();
        return mapper.toCommand(ticker, results.getFirst());
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
}

