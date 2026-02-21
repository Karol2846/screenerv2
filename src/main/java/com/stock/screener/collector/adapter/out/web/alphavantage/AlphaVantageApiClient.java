package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.stock.screener.collector.adapter.out.web.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.OverviewResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/query")
@RegisterRestClient(configKey = "alphavantage-api")
@RegisterProvider(AlphaVantageApiKeyFilter.class)
interface AlphaVantageApiClient {

    @GET
    OverviewResponse getOverview(
            @QueryParam("function") String function,
            @QueryParam("symbol") String symbol
    );

    @GET
    BalanceSheetResponse getBalanceSheet(
            @QueryParam("function") String function,
            @QueryParam("symbol") String symbol
    );

    @GET
    IncomeStatementResponse getIncomeStatement(
            @QueryParam("function") String function,
            @QueryParam("symbol") String symbol
    );
}
