package com.stock.screener.adapter.web.out.yhfinance.clients;

import com.stock.screener.adapter.web.out.model.recommendation.RecommendationResponse;
import com.stock.screener.adapter.web.out.yhfinance.YhFinanceClientInterceptor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import static com.stock.screener.adapter.web.out.yhfinance.YhFinanceBasePath.V6_FINANCE;

/**
 * Klient REST do pobierania rekomendacji podobnych akcji z YH Finance API.
 * Endpoint: /v6/finance/recommendationsbysymbol/{symbol}
 */
@Path(V6_FINANCE)
@RegisterRestClient(configKey = "yhfinance-api")
@RegisterProvider(YhFinanceClientInterceptor.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface YhFinanceRecommendationClient {

    @GET
    @Path("/recommendationsbysymbol/{symbol}")
    RecommendationResponse getRecommendations(@PathParam("symbol") String symbol);
}

