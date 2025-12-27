package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.marketsummary.MarketSummaryResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.V6_FINANCE;

/**
 * Klient REST do pobierania podsumowania rynku z YH Finance API.
 * Endpoint: /v6/finance/quote/marketSummary
 */
@Path(V6_FINANCE + "/quote")
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceMarketSummaryClient {

    @GET
    @Path("/marketSummary")
    MarketSummaryResponse getMarketSummary(
            @QueryParam("region") @DefaultValue("US") String region,
            @QueryParam("lang") @DefaultValue("en") String lang
    );
}

