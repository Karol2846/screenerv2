package com.stock.screener.domain.valueobject.fixtures;

import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.snapshot.MarketDataSnapshot;

import java.math.BigDecimal;

public final class MarketDataSnapshotFixture {

    private BigDecimal currentPrice = new BigDecimal("150.00");
    private BigDecimal marketCap = new BigDecimal("1000000000");
    private BigDecimal revenueTTM = new BigDecimal("500000000");
    private BigDecimal forwardPeRatio = new BigDecimal("25.0");
    private BigDecimal targetPrice = new BigDecimal("180.00");
    private BigDecimal forwardEpsGrowth = new BigDecimal("15.0");
    private BigDecimal forwardRevenueGrowth = new BigDecimal("12.5");
    private AnalystRatings analystRatings = AnalystRatings.builder()
            .strongBuy(5)
            .buy(10)
            .hold(3)
            .sell(1)
            .strongSell(0)
            .build();

    private MarketDataSnapshotFixture() {}

    public static MarketDataSnapshotFixture aMarketDataSnapshot() {
        return new MarketDataSnapshotFixture();
    }

    public MarketDataSnapshotFixture withCurrentPrice(String currentPrice) {
        this.currentPrice = new BigDecimal(currentPrice);
        return this;
    }

    public MarketDataSnapshotFixture withCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
        return this;
    }

    public MarketDataSnapshotFixture withNullCurrentPrice() {
        this.currentPrice = null;
        return this;
    }

    public MarketDataSnapshotFixture withZeroCurrentPrice() {
        this.currentPrice = BigDecimal.ZERO;
        return this;
    }

    public MarketDataSnapshotFixture withMarketCap(String marketCap) {
        this.marketCap = new BigDecimal(marketCap);
        return this;
    }

    public MarketDataSnapshotFixture withMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
        return this;
    }

    public MarketDataSnapshotFixture withNullMarketCap() {
        this.marketCap = null;
        return this;
    }

    public MarketDataSnapshotFixture withRevenueTTM(BigDecimal revenueTTM) {
        this.revenueTTM = revenueTTM;
        return this;
    }

    public MarketDataSnapshotFixture withRevenueTTM(String revenueTTM) {
        return withRevenueTTM(new BigDecimal(revenueTTM));
    }

    public MarketDataSnapshotFixture withZeroRevenueTTM() {
        this.revenueTTM = BigDecimal.ZERO;
        return this;
    }

    public MarketDataSnapshotFixture withNullRevenueTTM() {
        this.revenueTTM = null;
        return this;
    }

    public MarketDataSnapshotFixture withForwardPeRatio(String forwardPeRatio) {
        this.forwardPeRatio = new BigDecimal(forwardPeRatio);
        return this;
    }

    public MarketDataSnapshotFixture withForwardPeRatio(BigDecimal forwardPeRatio) {
        this.forwardPeRatio = forwardPeRatio;
        return this;
    }

    public MarketDataSnapshotFixture withNullForwardPeRatio() {
        this.forwardPeRatio = null;
        return this;
    }

    public MarketDataSnapshotFixture withTargetPrice(String targetPrice) {
        this.targetPrice = new BigDecimal(targetPrice);
        return this;
    }

    public MarketDataSnapshotFixture withTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
        return this;
    }

    public MarketDataSnapshotFixture withNullTargetPrice() {
        this.targetPrice = null;
        return this;
    }

    public MarketDataSnapshotFixture withForwardEpsGrowth(String forwardEpsGrowth) {
        this.forwardEpsGrowth = new BigDecimal(forwardEpsGrowth);
        return this;
    }

    public MarketDataSnapshotFixture withForwardEpsGrowth(BigDecimal forwardEpsGrowth) {
        this.forwardEpsGrowth = forwardEpsGrowth;
        return this;
    }

    public MarketDataSnapshotFixture withZeroForwardEpsGrowth() {
        this.forwardEpsGrowth = BigDecimal.ZERO;
        return this;
    }

    public MarketDataSnapshotFixture withNullForwardEpsGrowth() {
        this.forwardEpsGrowth = null;
        return this;
    }

    public MarketDataSnapshotFixture withForwardRevenueGrowth(String forwardRevenueGrowth) {
        this.forwardRevenueGrowth = new BigDecimal(forwardRevenueGrowth);
        return this;
    }

    public MarketDataSnapshotFixture withForwardRevenueGrowth(BigDecimal forwardRevenueGrowth) {
        this.forwardRevenueGrowth = forwardRevenueGrowth;
        return this;
    }

    public MarketDataSnapshotFixture withNullForwardRevenueGrowth() {
        this.forwardRevenueGrowth = null;
        return this;
    }

    public MarketDataSnapshotFixture withAnalystRatings(AnalystRatings analystRatings) {
        this.analystRatings = analystRatings;
        return this;
    }

    public MarketDataSnapshotFixture withNullAnalystRatings() {
        this.analystRatings = null;
        return this;
    }

    public static MarketDataSnapshotFixture avOnlySnapshot() {
        return new MarketDataSnapshotFixture()
                .withNullForwardEpsGrowth()
                .withNullForwardRevenueGrowth()
                .withNullAnalystRatings();
    }

    public static MarketDataSnapshotFixture yhOnlySnapshot() {
        return new MarketDataSnapshotFixture()
                .withNullCurrentPrice()
                .withNullMarketCap()
                .withNullRevenueTTM()
                .withNullForwardPeRatio()
                .withNullTargetPrice();
    }

    public MarketDataSnapshot build() {
        return MarketDataSnapshot.builder()
                .currentPrice(currentPrice)
                .marketCap(marketCap)
                .revenueTTM(revenueTTM)
                .forwardPeRatio(forwardPeRatio)
                .targetPrice(targetPrice)
                .forwardEpsGrowth(forwardEpsGrowth)
                .forwardRevenueGrowth(forwardRevenueGrowth)
                .analystRatings(analystRatings)
                .build();
    }
}

