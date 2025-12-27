package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.options.OptionChainResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.V7_FINANCE;

/**
 * Klient REST do pobierania danych o opcjach z YH Finance API.
 * Endpoint: /v7/finance/options/{symbol}
 */
@Path(V7_FINANCE)
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceOptionsClient {

    @GET
    @Path("/options/{symbol}")
    OptionChainResponse getOptions(
            @PathParam("symbol") String symbol,
            @QueryParam("date") Long date
    );

    @GET
    @Path("/options/{symbol}")
    OptionChainResponse getOptions(@PathParam("symbol") String symbol);
}

