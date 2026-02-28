package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;

import java.math.BigDecimal;

public final class RawOverviewFixture {

    private String symbol = "AAPL";
    private String assetType = "Common Stock";
    private String name = "Apple Inc";
    private String exchange = "NASDAQ";
    private String currency = "USD";
    private String country = "USA";
    private String sector = "TECHNOLOGY";
    private String industry = "Consumer Electronics";
    private BigDecimal marketCapitalization = new BigDecimal("2800000000000");
    private BigDecimal ebitda = new BigDecimal("134642000000");
    private BigDecimal peRatio = new BigDecimal("28.50");
    private BigDecimal pegRatio = new BigDecimal("2.10");
    private BigDecimal bookValue = new BigDecimal("4.38");
    private BigDecimal dividendPerShare = new BigDecimal("0.96");
    private BigDecimal dividendYield = new BigDecimal("0.0055");
    private BigDecimal eps = new BigDecimal("6.13");
    private BigDecimal revenuePerShareTTM = new BigDecimal("25.31");
    private BigDecimal profitMargin = new BigDecimal("0.2397");
    private BigDecimal operatingMarginTTM = new BigDecimal("0.3031");
    private BigDecimal returnOnAssetsTTM = new BigDecimal("0.2571");
    private BigDecimal returnOnEquityTTM = new BigDecimal("1.6095");
    private BigDecimal revenueTTM = new BigDecimal("391035000000");
    private BigDecimal grossProfitTTM = new BigDecimal("180683000000");
    private BigDecimal quarterlyEarningsGrowthYOY = new BigDecimal("0.124");
    private BigDecimal quarterlyRevenueGrowthYOY = new BigDecimal("0.061");
    private BigDecimal analystTargetPrice = new BigDecimal("235.00");
    private Integer analystRatingStrongBuy = 12;
    private Integer analystRatingBuy = 20;
    private Integer analystRatingHold = 8;
    private Integer analystRatingSell = 2;
    private Integer analystRatingStrongSell = 1;
    private BigDecimal trailingPE = new BigDecimal("28.50");
    private BigDecimal forwardPE = new BigDecimal("25.00");
    private BigDecimal priceToSalesRatioTTM = new BigDecimal("7.16");
    private BigDecimal priceToBookRatio = new BigDecimal("39.87");
    private BigDecimal evToRevenue = new BigDecimal("7.51");
    private BigDecimal evToEBITDA = new BigDecimal("21.80");
    private BigDecimal beta = new BigDecimal("1.25");
    private BigDecimal weekHigh52 = new BigDecimal("199.62");
    private BigDecimal weekLow52 = new BigDecimal("164.08");
    private BigDecimal movingAverage50Day = new BigDecimal("178.12");
    private BigDecimal movingAverage200Day = new BigDecimal("181.45");
    private BigDecimal sharesOutstanding = new BigDecimal("15461900000");
    private String fiscalYearEnd = "September";

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
