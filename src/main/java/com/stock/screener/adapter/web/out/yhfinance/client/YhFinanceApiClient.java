package com.stock.screener.adapter.web.out.yhfinance.client;

import com.stock.screener.adapter.web.out.yhfinance.exception.YhFinanceExceptionMapper;
import com.stock.screener.adapter.web.out.yhfinance.model.QuoteSummaryResponse;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v11/finance")
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceExceptionMapper.class)
@ClientHeaderParam(name = "X-API-KEY", value = "${yhfinance.api.key}")
public interface YhFinanceApiClient {

    @GET
    @Path("/quoteSummary/{symbol}")
    @RunOnVirtualThread
    QuoteSummaryResponse getQuoteSummary(
            @PathParam("symbol") String symbol,
            @QueryParam("modules") String modules,
            @QueryParam("lang") String lang,
            @QueryParam("region") String region
    );
}

