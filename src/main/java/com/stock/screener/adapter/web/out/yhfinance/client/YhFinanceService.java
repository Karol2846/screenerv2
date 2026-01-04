package com.stock.screener.adapter.web.out.yhfinance.client;

import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResponse;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jspecify.annotations.NonNull;

import java.util.List;


@ApplicationScoped
public class YhFinanceService {

    private final YhFinanceApiClient apiClient;

    String DEFAULT_MODULES = "earningsTrend,recommendationTrend,financialData,incomeStatementHistoryQuarterly";
    String DEFAULT_LANG = "en";
    String DEFAULT_REGION = "US";

    @Inject
    public YhFinanceService(@RestClient YhFinanceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public QuoteSummaryResult getFinancialData(@NonNull String symbol) {
        QuoteSummaryResponse response = apiClient.getQuoteSummary(symbol, DEFAULT_MODULES, DEFAULT_LANG, DEFAULT_REGION);
        validateClientResponse(symbol, response);

        List<QuoteSummaryResult> results = response.quoteSummary().result();
        return results.getFirst();
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

