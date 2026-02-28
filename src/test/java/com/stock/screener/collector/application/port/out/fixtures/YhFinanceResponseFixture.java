package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.collector.domain.valueobject.AnalystRatings;

import java.math.BigDecimal;

public final class YhFinanceResponseFixture {

    private String ticker = "AAPL";
    private BigDecimal currentPrice = new BigDecimal("175.50");
    private BigDecimal forwardEpsGrowth = new BigDecimal("0.15");
    private BigDecimal forwardRevenueGrowth = new BigDecimal("0.12");
    private AnalystRatings analystRatings = AnalystRatings.builder()
            .strongBuy(10)
            .buy(15)
            .hold(5)
            .sell(2)
            .strongSell(1)
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
