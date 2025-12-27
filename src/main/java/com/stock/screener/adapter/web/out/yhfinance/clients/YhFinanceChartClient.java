package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.chart.ChartResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.V8_FINANCE;

/**
 * Klient REST do pobierania danych historycznych z YH Finance API.
 * Endpoint: /v8/finance/chart/{ticker}
 */
@Path(V8_FINANCE)
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceChartClient {

    @GET
    @Path("/chart/{ticker}")
    ChartResponse getChart(
            @PathParam("ticker") String ticker,
            @QueryParam("interval") @DefaultValue("1d") String interval,
            @QueryParam("range") @DefaultValue("1mo") String range,
            @QueryParam("comparisons") String comparisons,
            @QueryParam("region") @DefaultValue("US") String region,
            @QueryParam("lang") @DefaultValue("en") String lang,
            @QueryParam("events") String events
    );

    @GET
    @Path("/chart/{ticker}")
    ChartResponse getChart(
            @PathParam("ticker") String ticker,
            @QueryParam("interval") @DefaultValue("1d") String interval,
            @QueryParam("range") @DefaultValue("1mo") String range
    );
}

