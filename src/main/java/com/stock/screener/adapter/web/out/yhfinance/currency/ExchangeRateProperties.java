package com.stock.screener.adapter.web.out.yhfinance.currency;

import io.smallrye.config.ConfigMapping;

import java.math.BigDecimal;
import java.util.Map;

@ConfigMapping(prefix = "exahnge-rates")
public interface ExchangeRateProperties {

    Map<String, BigDecimal> rates();

    default BigDecimal getRateFor(String currency) {
        return rates().get(currency);
    }

}
