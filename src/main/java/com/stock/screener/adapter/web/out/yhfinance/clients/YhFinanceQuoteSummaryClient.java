package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.quotesummary.QuoteSummaryResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.V11_FINANCE;

/**
 * Klient REST do pobierania szczegolowych danych o instrumencie z YH Finance API.
 * Endpoint: /v11/finance/quoteSummary/{symbol}
 */
@Path(V11_FINANCE)
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceQuoteSummaryClient {

    @GET
    @Path("/quoteSummary/{symbol}")
    QuoteSummaryResponse getQuoteSummary(
            @PathParam("symbol") String symbol,
            @QueryParam("modules") String modules,
            @QueryParam("region") @DefaultValue("US") String region,
            @QueryParam("lang") @DefaultValue("en") String lang
    );
}

