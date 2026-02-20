package com.stock.screener.adapter.web.out.alphavantage;

import com.stock.screener.application.port.out.alphavantage.BalanceSheetResponse;
import com.stock.screener.application.port.out.alphavantage.IncomeStatementResponse;
import com.stock.screener.application.port.out.alphavantage.OverviewResponse;
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
