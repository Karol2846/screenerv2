package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.insights.InsightsResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.WS_INSIGHTS;

/**
 * Klient REST do pobierania analiz i raportow z YH Finance API.
 * Endpoint: /ws/insights/v1/finance/insights
 */
@Path(WS_INSIGHTS)
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceInsightsClient {

    @GET
    @Path("/insights")
    InsightsResponse getInsights(@QueryParam("symbol") String symbol);
}

