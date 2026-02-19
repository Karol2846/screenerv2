package com.stock.screener.domain.valueobject.fixtures;

import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;

import java.math.BigDecimal;

public final class FinancialDataSnapshotFixture {

    private BigDecimal marketCapitalization = new BigDecimal("2000000000");
    private BigDecimal totalCurrentAssets = new BigDecimal("500000");
    private BigDecimal totalCurrentLiabilities = new BigDecimal("200000");
    private BigDecimal totalAssets = new BigDecimal("1000000");
    private BigDecimal totalLiabilities = new BigDecimal("400000");
    private BigDecimal retainedEarnings = new BigDecimal("150000");
    private BigDecimal ebit = new BigDecimal("100000");
    private BigDecimal interestExpense = new BigDecimal("50000");
    private BigDecimal totalShareholderEquity = new BigDecimal("600000");
    private BigDecimal inventory = new BigDecimal("100000");
    private BigDecimal totalRevenue = new BigDecimal("800000");

    private FinancialDataSnapshotFixture() {}

    public static FinancialDataSnapshotFixture aFinancialDataSnapshot() {
        return new FinancialDataSnapshotFixture();
    }

    public FinancialDataSnapshotFixture withMarketCapitalization(String marketCapitalization) {
        this.marketCapitalization = new BigDecimal(marketCapitalization);
        return this;
    }

    public FinancialDataSnapshotFixture withTotalCurrentAssets(String totalCurrentAssets) {
        this.totalCurrentAssets = new BigDecimal(totalCurrentAssets);
        return this;
    }

    public FinancialDataSnapshotFixture withNullTotalCurrentAssets() {
        this.totalCurrentAssets = null;
        return this;
    }

    public FinancialDataSnapshotFixture withTotalCurrentLiabilities(String totalCurrentLiabilities) {
        this.totalCurrentLiabilities = new BigDecimal(totalCurrentLiabilities);
        return this;
    }

    public FinancialDataSnapshotFixture withNullTotalCurrentLiabilities() {
        this.totalCurrentLiabilities = null;
        return this;
    }

    public FinancialDataSnapshotFixture withEbit(String ebit) {
        this.ebit = new BigDecimal(ebit);
        return this;
    }

    public FinancialDataSnapshotFixture withNullEbit() {
        this.ebit = null;
        return this;
    }

    public FinancialDataSnapshotFixture withInterestExpense(String interestExpense) {
        this.interestExpense = new BigDecimal(interestExpense);
        return this;
    }

    public FinancialDataSnapshotFixture withNullInterestExpense() {
        this.interestExpense = null;
        return this;
    }

    public FinancialDataSnapshotFixture withInventory(BigDecimal inventory) {
        this.inventory = inventory;
        return this;
    }

    public FinancialDataSnapshotFixture withInventory(String inventory) {
        return withInventory(new BigDecimal(inventory));
    }

    public FinancialDataSnapshotFixture withNullInventory() {
        this.inventory = null;
        return this;
    }

    public FinancialDataSnapshotFixture withTotalAssets(String totalAssets) {
        this.totalAssets = new BigDecimal(totalAssets);
        return this;
    }

    public FinancialDataSnapshotFixture withNullTotalAssets() {
        this.totalAssets = null;
        return this;
    }

    public FinancialDataSnapshotFixture withTotalLiabilities(String totalLiabilities) {
        this.totalLiabilities = new BigDecimal(totalLiabilities);
        return this;
    }

    public FinancialDataSnapshotFixture withNullTotalLiabilities() {
        this.totalLiabilities = null;
        return this;
    }

    public FinancialDataSnapshotFixture withRetainedEarnings(String retainedEarnings) {
        this.retainedEarnings = new BigDecimal(retainedEarnings);
        return this;
    }

    public FinancialDataSnapshotFixture withNullRetainedEarnings() {
        this.retainedEarnings = null;
        return this;
    }

    public FinancialDataSnapshotFixture withTotalShareholderEquity(String totalShareholderEquity) {
        this.totalShareholderEquity = new BigDecimal(totalShareholderEquity);
        return this;
    }

    public FinancialDataSnapshotFixture withNullTotalShareholderEquity() {
        this.totalShareholderEquity = null;
        return this;
    }

    public FinancialDataSnapshotFixture withTotalRevenue(String totalRevenue) {
        this.totalRevenue = new BigDecimal(totalRevenue);
        return this;
    }

    public FinancialDataSnapshotFixture withNullTotalRevenue() {
        this.totalRevenue = null;
        return this;
    }

    public FinancialDataSnapshot build() {
        return FinancialDataSnapshot.builder()
                .marketCapitalization(marketCapitalization)
                .totalCurrentAssets(totalCurrentAssets)
                .totalCurrentLiabilities(totalCurrentLiabilities)
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .retainedEarnings(retainedEarnings)
                .ebit(ebit)
                .interestExpense(interestExpense)
                .totalShareholderEquity(totalShareholderEquity)
                .inventory(inventory)
                .totalRevenue(totalRevenue)
                .build();
    }
}

