package com.stock.screener.collector.adapter.in.web;

import com.stock.screener.collector.application.port.in.CollectMonthlyDataUseCase;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/api/collector/monthly")
@RequiredArgsConstructor
class MonthlyCollectorController {

    private final CollectMonthlyDataUseCase collectMonthlyDataUseCase;
    private final TickerReaderPort tickerReaderPort;

    @POST
    @Path("/{ticker}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response collectForTicker(@PathParam("ticker") String ticker) {
        log.info("Manual monthly collection triggered for: {}", ticker);
        try {
            collectMonthlyDataUseCase.collectMonthlyData(ticker.toUpperCase());
            return Response.ok().build();
        } catch (Exception ex) {
            log.error("Monthly collection failed for ticker: {}", ticker, ex);
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @POST
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response collectAll() {
        log.info("Manual monthly collection triggered for all tickers");
        var tickers = tickerReaderPort.readTickers();
        int success = 0;
        int failed = 0;

        for (String ticker : tickers) {
            try {
                collectMonthlyDataUseCase.collectMonthlyData(ticker);
                success++;
            } catch (Exception ex) {
                log.error("Monthly collection failed for ticker: {}", ticker, ex);
                failed++;
            }
        }

        return Response.ok()
                .entity("{\"success\":" + success + ",\"failed\":" + failed + "}")
                .build();
    }
}
