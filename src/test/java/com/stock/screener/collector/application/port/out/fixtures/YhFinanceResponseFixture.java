package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.collector.domain.valueobject.AnalystRatings;

import java.math.BigDecimal;

public final class YhFinanceResponseFixture {

    private String ticker = "META";
    private BigDecimal currentPrice = new BigDecimal("648.18");
    private BigDecimal forwardEpsGrowth = new BigDecimal("0.1866");
    private BigDecimal forwardRevenueGrowth = new BigDecimal("0.177");
    private AnalystRatings analystRatings = AnalystRatings.builder()
            .strongBuy(11)
            .buy(51)
            .hold(5)
            .sell(0)
            .strongSell(0)
            .build();

    private YhFinanceResponseFixture() {}

    public static YhFinanceResponseFixture aYhFinanceResponse() {
        return new YhFinanceResponseFixture();
    }

    public YhFinanceResponseFixture withTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public YhFinanceResponseFixture withCurrentPrice(String value) {
        this.currentPrice = new BigDecimal(value);
        return this;
    }

    public YhFinanceResponseFixture withNullCurrentPrice() {
        this.currentPrice = null;
        return this;
    }

    public YhFinanceResponseFixture withForwardEpsGrowth(String value) {
        this.forwardEpsGrowth = new BigDecimal(value);
        return this;
    }

    public YhFinanceResponseFixture withNullForwardEpsGrowth() {
        this.forwardEpsGrowth = null;
        return this;
    }

    public YhFinanceResponseFixture withForwardRevenueGrowth(String value) {
        this.forwardRevenueGrowth = new BigDecimal(value);
        return this;
    }

    public YhFinanceResponseFixture withNullForwardRevenueGrowth() {
        this.forwardRevenueGrowth = null;
        return this;
    }

    public YhFinanceResponseFixture withAnalystRatings(AnalystRatings ratings) {
        this.analystRatings = ratings;
        return this;
    }

    public YhFinanceResponseFixture withNullAnalystRatings() {
        this.analystRatings = null;
        return this;
    }

    public YhFinanceResponse build() {
        return YhFinanceResponse.builder()
                .ticker(ticker)
                .currentPrice(currentPrice)
                .forwardEpsGrowth(forwardEpsGrowth)
                .forwardRevenueGrowth(forwardRevenueGrowth)
                .analystRatings(analystRatings)
                .build();
    }
}
