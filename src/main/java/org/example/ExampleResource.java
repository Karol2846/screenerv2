package org.example;

import com.stock.screener.adapter.web.out.yhfinance.currency.ExchangeRateProperties;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Path("/hello")
@RequiredArgsConstructor
public class ExampleResource {

    private final ExchangeRateProperties properties;

    @GET
    @Path("/{currency}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("currency") String currency) {
        return properties.getRateFor(currency).toString();
    }
}
