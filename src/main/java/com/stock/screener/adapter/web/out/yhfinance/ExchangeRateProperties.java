package com.stock.screener.adapter.web.out.yhfinance;

import io.smallrye.config.ConfigMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@ConfigMapping(prefix = "exchange-rates")
public interface ExchangeRateProperties {

    Map<String, BigDecimal> rates();

    private BigDecimal getRateFor(String currency) {
        return rates().getOrDefault(currency, BigDecimal.ZERO);
    }

    default BigDecimal calculateInUsd(String currency, BigDecimal amount) {
        BigDecimal rate = getRateFor(currency);
        if (rate.equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("No exchange rate found for currency: " + currency);
        }
        return amount.divide(rate, RoundingMode.HALF_UP);
    }

}
