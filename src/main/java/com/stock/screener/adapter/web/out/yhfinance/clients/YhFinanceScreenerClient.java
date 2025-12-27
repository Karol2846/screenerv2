package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.screener.ScreenerResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.WS_SCREENERS;

/**
 * Klient REST do pobierania danych ze screenera z YH Finance API.
 * Endpoint: /ws/screeners/v1/finance/screener/predefined/saved
 */
@Path(WS_SCREENERS)
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceScreenerClient {

    @GET
    @Path("/predefined/saved")
    ScreenerResponse getScreenerResults(
            @QueryParam("scrIds") String scrIds,
            @QueryParam("count") @DefaultValue("25") Integer count
    );
}

