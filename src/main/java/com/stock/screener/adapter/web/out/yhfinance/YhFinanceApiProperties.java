package com.stock.screener.adapter.web.out.yhfinance;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "yhfinance.api")
public interface YhFinanceApiProperties {
    String key();
    String baseUrl();
}
