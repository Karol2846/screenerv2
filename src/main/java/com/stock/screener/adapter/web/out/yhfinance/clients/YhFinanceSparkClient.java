package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.spark.SparkResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.V8_FINANCE;

/**
 * Klient REST do pobierania uproszczonych danych historycznych z YH Finance API.
 * Endpoint: /v8/finance/spark
 */
@Path(V8_FINANCE)
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceSparkClient {

    @GET
    @Path("/spark")
    SparkResponse getSpark(
            @QueryParam("symbols") String symbols,
            @QueryParam("interval") @DefaultValue("1d") String interval,
            @QueryParam("range") @DefaultValue("1mo") String range
    );
}

