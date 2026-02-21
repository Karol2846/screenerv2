package com.stock.screener.collector.adapter.out.web.alphavantage;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

class AlphaVantageApiKeyFilter implements ClientRequestFilter {

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
