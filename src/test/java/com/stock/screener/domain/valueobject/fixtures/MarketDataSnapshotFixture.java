package com.stock.screener.domain.valueobject.fixtures;

import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;

import java.math.BigDecimal;

public final class MarketDataSnapshotFixture {

    private BigDecimal currentPrice = new BigDecimal("150.00");
    private BigDecimal marketCap = new BigDecimal("1000000000");
    private BigDecimal revenueTTM = new BigDecimal("500000000");
    private BigDecimal forwardPeRatio = new BigDecimal("25.0");
    private BigDecimal targetPrice = new BigDecimal("180.00");
    private BigDecimal forwardEpsGrowth = new BigDecimal("15.0");
    private BigDecimal forwardRevenueGrowth = new BigDecimal("12.5");

    private MarketDataSnapshotFixture() {}

    public static MarketDataSnapshotFixture aMarketDataSnapshot() {
        return new MarketDataSnapshotFixture();
    }

    public MarketDataSnapshotFixture withCurrentPrice(String currentPrice) {
        this.currentPrice = new BigDecimal(currentPrice);
        return this;
    }

    public MarketDataSnapshotFixture withNullCurrentPrice() {
        this.currentPrice = null;
        return this;
    }

    public MarketDataSnapshotFixture withMarketCap(String marketCap) {
        this.marketCap = new BigDecimal(marketCap);
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

    public MarketDataSnapshotFixture withNullRevenueTTM() {
        this.revenueTTM = null;
        return this;
    }

    public MarketDataSnapshotFixture withForwardPeRatio(String forwardPeRatio) {
        this.forwardPeRatio = new BigDecimal(forwardPeRatio);
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

    public MarketDataSnapshotFixture withNullTargetPrice() {
        this.targetPrice = null;
        return this;
    }

    public MarketDataSnapshotFixture withForwardEpsGrowth(String forwardEpsGrowth) {
        this.forwardEpsGrowth = new BigDecimal(forwardEpsGrowth);
        return this;
    }

    public MarketDataSnapshotFixture withNullForwardEpsGrowth() {
        this.forwardEpsGrowth = null;
        return this;
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
                .build();
    }
}

