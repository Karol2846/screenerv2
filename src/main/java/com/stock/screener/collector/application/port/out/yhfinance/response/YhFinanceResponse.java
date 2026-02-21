package com.stock.screener.collector.application.port.out.yhfinance.response;

import com.stock.screener.domain.valueobject.AnalystRatings;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record YhFinanceResponse(
    String ticker,
    BigDecimal forwardEpsGrowth,
    BigDecimal forwardRevenueGrowth,
    AnalystRatings analystRatings
) {}
