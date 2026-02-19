package com.stock.screener.application.port.out.yhfinance.command;

import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.Sector;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record QuoteSummaryCommand(
    String ticker,
    Sector sector,
    BigDecimal currentPrice,
    BigDecimal marketCap,
    BigDecimal forwardPeRatio,
    BigDecimal forwardRevenue,  //forwardPs = marketCap / forwardRevenue`

    BigDecimal forwardRevenueGrowth,
    BigDecimal forwardEpsGrowth,
    BigDecimal targetPrice,
    AnalystRatings analystRatings,

    BigDecimal operatingCashFlow
) {}
