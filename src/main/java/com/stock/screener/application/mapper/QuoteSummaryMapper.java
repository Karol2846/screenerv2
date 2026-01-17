package com.stock.screener.application.mapper;

import com.stock.screener.application.port.out.command.QuoteSummaryCommand;
import com.stock.screener.domain.valueobject.MarketData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class QuoteSummaryMapper {

    public static MarketData toMarketData(QuoteSummaryCommand command) {
        return MarketData.builder()
                .currentPrice(command.currentPrice())
                .marketCap(command.marketCap())
                .forwardPeRatio(command.forwardPeRatio())
                .psRatio(command.marketCap().divide(command.forwardRevenue(), RoundingMode.HALF_UP))

                //TODO: is it correct to use epsGrowth? it's value or percentage?
                // - also...when epsGrowth = 0 -> peg = 1 ???
                .forwardPegRatio(command.forwardPeRatio().divide(
                        command.forwardEpsGrowth().compareTo(BigDecimal.ZERO) == 0 ?
                                BigDecimal.ONE : command.forwardEpsGrowth(), RoundingMode.HALF_UP))
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
