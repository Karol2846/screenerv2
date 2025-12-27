package com.stock.screener.adapter.web.out.yhfinance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.RequiredArgsConstructor;

/**
 * Producer dla komponentow REST Client YH Finance.
 */
@ApplicationScoped
@RequiredArgsConstructor
public class YhFinanceClientProducer {

    private final YhFinanceApiProperties apiProperties;

    @Produces
    @ApplicationScoped
    public YhFinanceClientInterceptor yhFinanceClientInterceptor() {
        return new YhFinanceClientInterceptor(apiProperties);
    }
}

