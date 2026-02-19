package com.stock.screener.adapter.web.out.alphavantage;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

public class AlphaVantageApiKeyFilter implements ClientRequestFilter {

    @ConfigProperty(name = "alphavantage.api.key")
    String apiKey;

    @Override
    public void filter(ClientRequestContext requestContext) {
        URI uri = UriBuilder.fromUri(requestContext.getUri())
                .queryParam("apikey", apiKey)
                .build();
        requestContext.setUri(uri);
    }
}
