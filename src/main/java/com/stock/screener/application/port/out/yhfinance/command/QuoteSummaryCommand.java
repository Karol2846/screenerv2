package com.stock.screener.application.port.out.yhfinance.command;

import com.stock.screener.domain.valueobject.AnalystRatings;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record QuoteSummaryCommand(
    String ticker,
    BigDecimal forwardEpsGrowth,
    BigDecimal forwardRevenueGrowth,
    AnalystRatings analystRatings
) {}
