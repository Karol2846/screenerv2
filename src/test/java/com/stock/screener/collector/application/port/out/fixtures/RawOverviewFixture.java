package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;

import java.math.BigDecimal;

public final class RawOverviewFixture {

    // Defaults aligned with integrationTest/resources/stubs/overview.json (META)
    private String symbol = "META";
    private String assetType = "Common Stock";
    private String name = "Meta Platforms Inc.";
    private String exchange = "NASDAQ";
    private String currency = "USD";
    private String country = "USA";
    private String sector = "COMMUNICATION SERVICES";
    private String industry = "INTERNET CONTENT & INFORMATION";
    private BigDecimal marketCapitalization = new BigDecimal("1661943284000");
    private BigDecimal ebitda = new BigDecimal("101891998000");
    private BigDecimal peRatio = new BigDecimal("27.93");
    private BigDecimal pegRatio = new BigDecimal("1.149");
    private BigDecimal bookValue = new BigDecimal("85.87");
    private BigDecimal dividendPerShare = new BigDecimal("2.1");
    private BigDecimal dividendYield = new BigDecimal("0.0032");
    private BigDecimal eps = new BigDecimal("23.52");
    private BigDecimal revenuePerShareTTM = new BigDecimal("79.72");
    private BigDecimal profitMargin = new BigDecimal("0.301");
    private BigDecimal operatingMarginTTM = new BigDecimal("0.413");
    private BigDecimal returnOnAssetsTTM = new BigDecimal("0.162");
    private BigDecimal returnOnEquityTTM = new BigDecimal("0.302");
    private BigDecimal revenueTTM = new BigDecimal("200965997000");
    private BigDecimal grossProfitTTM = new BigDecimal("164790993000");
    private BigDecimal quarterlyEarningsGrowthYOY = new BigDecimal("0.107");
    private BigDecimal quarterlyRevenueGrowthYOY = new BigDecimal("0.238");
    private BigDecimal analystTargetPrice = new BigDecimal("861.42");
    private Integer analystRatingStrongBuy = 11;
    private Integer analystRatingBuy = 51;
    private Integer analystRatingHold = 5;
    private Integer analystRatingSell = 0;
    private Integer analystRatingStrongSell = 0;
    private BigDecimal trailingPE = new BigDecimal("27.93");
    private BigDecimal forwardPE = new BigDecimal("21.28");
    private BigDecimal priceToSalesRatioTTM = new BigDecimal("8.27");
    private BigDecimal priceToBookRatio = new BigDecimal("7.61");
    private BigDecimal evToRevenue = new BigDecimal("8.24");
    private BigDecimal evToEBITDA = new BigDecimal("15.66");
    private BigDecimal beta = new BigDecimal("1.284");
    private BigDecimal weekHigh52 = new BigDecimal("795.06");
    private BigDecimal weekLow52 = new BigDecimal("478.72");
    private BigDecimal movingAverage50Day = new BigDecimal("656.43");
    private BigDecimal movingAverage200Day = new BigDecimal("690.93");
    private BigDecimal sharesOutstanding = new BigDecimal("2187178000");
    private String fiscalYearEnd = "December";

    private RawOverviewFixture() {}

    public static RawOverviewFixture aRawOverview() {
        return new RawOverviewFixture();
    }

    public RawOverviewFixture withMarketCapitalization(String value) {
        this.marketCapitalization = new BigDecimal(value);
        return this;
    }

    public RawOverviewFixture withNullMarketCapitalization() {
        this.marketCapitalization = null;
        return this;
    }

    public RawOverviewFixture withRevenueTTM(String value) {
        this.revenueTTM = new BigDecimal(value);
        return this;
    }

    public RawOverviewFixture withNullRevenueTTM() {
        this.revenueTTM = null;
        return this;
    }

    public RawOverviewFixture withForwardPE(String value) {
        this.forwardPE = new BigDecimal(value);
        return this;
    }

    public RawOverviewFixture withNullForwardPE() {
        this.forwardPE = null;
        return this;
    }

    public RawOverviewFixture withAnalystTargetPrice(String value) {
        this.analystTargetPrice = new BigDecimal(value);
        return this;
    }

    public RawOverviewFixture withNullAnalystTargetPrice() {
        this.analystTargetPrice = null;
        return this;
    }

    public RawOverview build() {
        return RawOverview.builder()
                .symbol(symbol)
                .assetType(assetType)
                .name(name)
                .exchange(exchange)
                .currency(currency)
                .country(country)
                .sector(sector)
                .industry(industry)
                .marketCapitalization(marketCapitalization)
                .ebitda(ebitda)
                .peRatio(peRatio)
                .pegRatio(pegRatio)
                .bookValue(bookValue)
                .dividendPerShare(dividendPerShare)
                .dividendYield(dividendYield)
                .eps(eps)
                .revenuePerShareTTM(revenuePerShareTTM)
                .profitMargin(profitMargin)
                .operatingMarginTTM(operatingMarginTTM)
                .returnOnAssetsTTM(returnOnAssetsTTM)
                .returnOnEquityTTM(returnOnEquityTTM)
                .revenueTTM(revenueTTM)
                .grossProfitTTM(grossProfitTTM)
                .quarterlyEarningsGrowthYOY(quarterlyEarningsGrowthYOY)
                .quarterlyRevenueGrowthYOY(quarterlyRevenueGrowthYOY)
                .analystTargetPrice(analystTargetPrice)
                .analystRatingStrongBuy(analystRatingStrongBuy)
                .analystRatingBuy(analystRatingBuy)
                .analystRatingHold(analystRatingHold)
                .analystRatingSell(analystRatingSell)
                .analystRatingStrongSell(analystRatingStrongSell)
                .trailingPE(trailingPE)
                .forwardPE(forwardPE)
                .priceToSalesRatioTTM(priceToSalesRatioTTM)
                .priceToBookRatio(priceToBookRatio)
                .evToRevenue(evToRevenue)
                .evToEBITDA(evToEBITDA)
                .beta(beta)
                .weekHigh52(weekHigh52)
                .weekLow52(weekLow52)
                .movingAverage50Day(movingAverage50Day)
                .movingAverage200Day(movingAverage200Day)
                .sharesOutstanding(sharesOutstanding)
                .fiscalYearEnd(fiscalYearEnd)
                .build();
    }
}
