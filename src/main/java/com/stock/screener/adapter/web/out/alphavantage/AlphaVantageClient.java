package com.stock.screener.adapter.web.out.alphavantage;

import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.OverviewResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/query")
@RegisterRestClient(configKey = "alphavantage-api")
public interface AlphaVantageClient {

    @GET
    OverviewResponse getOverview(
            @QueryParam("function") String function,
            @QueryParam("symbol") String symbol,
            @QueryParam("apikey") String apiKey
    );

    @GET
    BalanceSheetResponse getBalanceSheet(
            @QueryParam("function") String function,
            @QueryParam("symbol") String symbol,
            @QueryParam("apikey") String apiKey
    );

    @GET
    IncomeStatementResponse getIncomeStatement(
            @QueryParam("function") String function,
            @QueryParam("symbol") String symbol,
            @QueryParam("apikey") String apiKey
    );
}
