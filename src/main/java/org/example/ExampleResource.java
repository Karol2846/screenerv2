package org.example;

import com.stock.screener.application.port.out.yhfinance.command.QuoteSummaryCommand;
import com.stock.screener.application.port.out.yhfinance.YahooFinanceClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/stock/{ticker}")
@RequiredArgsConstructor
public class ExampleResource {

    private final YahooFinanceClient client;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public QuoteSummaryCommand hello(@PathParam("ticker") String ticker) {
        return client.getQuoteSummary(ticker);
    }
}
