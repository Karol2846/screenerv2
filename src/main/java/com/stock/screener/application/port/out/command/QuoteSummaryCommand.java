package com.stock.screener.application.port.out.command;

import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.Sector;

import java.math.BigDecimal;

//kwartalne będą pobierane tylko z alphaVantage
public record QuoteSummaryCommand(
    String ticker,
    Sector sector,
    BigDecimal currentPrice,
    BigDecimal marketCap,
    BigDecimal forwardPeRatio,
    BigDecimal psRatio,

    BigDecimal forwardRevenueGrowth,
    BigDecimal forwardEpsGrowth,
    BigDecimal targetPrice,
    AnalystRatings analystRatings
) {}
