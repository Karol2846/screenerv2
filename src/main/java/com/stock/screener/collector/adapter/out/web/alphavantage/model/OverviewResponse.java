package com.stock.screener.collector.adapter.out.web.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OverviewResponse(
        @JsonProperty("Symbol") String symbol,
        @JsonProperty("AssetType") String assetType,
        @JsonProperty("Name") String name,
        @JsonProperty("Exchange") String exchange,
        @JsonProperty("Currency") String currency,
        @JsonProperty("Country") String country,
        @JsonProperty("Sector") String sector,
        @JsonProperty("Industry") String industry,
        @JsonProperty("MarketCapitalization") BigDecimal marketCapitalization,
        @JsonProperty("EBITDA") BigDecimal ebitda,
        @JsonProperty("PERatio") BigDecimal peRatio,
        @JsonProperty("PEGRatio") BigDecimal pegRatio,
        @JsonProperty("BookValue") BigDecimal bookValue,
        @JsonProperty("DividendPerShare") BigDecimal dividendPerShare,
        @JsonProperty("DividendYield") BigDecimal dividendYield,
        @JsonProperty("EPS") BigDecimal eps,
        @JsonProperty("RevenuePerShareTTM") BigDecimal revenuePerShareTTM,
        @JsonProperty("ProfitMargin") BigDecimal profitMargin,
        @JsonProperty("OperatingMarginTTM") BigDecimal operatingMarginTTM,
        @JsonProperty("ReturnOnAssetsTTM") BigDecimal returnOnAssetsTTM,
        @JsonProperty("ReturnOnEquityTTM") BigDecimal returnOnEquityTTM,
        @JsonProperty("RevenueTTM") BigDecimal revenueTTM,
        @JsonProperty("GrossProfitTTM") BigDecimal grossProfitTTM,
        @JsonProperty("QuarterlyEarningsGrowthYOY") BigDecimal quarterlyEarningsGrowthYOY,
        @JsonProperty("QuarterlyRevenueGrowthYOY") BigDecimal quarterlyRevenueGrowthYOY,
        @JsonProperty("AnalystTargetPrice") BigDecimal analystTargetPrice,
        @JsonProperty("AnalystRatingStrongBuy") Integer analystRatingStrongBuy,
        @JsonProperty("AnalystRatingBuy") Integer analystRatingBuy,
        @JsonProperty("AnalystRatingHold") Integer analystRatingHold,
        @JsonProperty("AnalystRatingSell") Integer analystRatingSell,
        @JsonProperty("AnalystRatingStrongSell") Integer analystRatingStrongSell,
        @JsonProperty("TrailingPE") BigDecimal trailingPE,
        @JsonProperty("ForwardPE") BigDecimal forwardPE,
        @JsonProperty("PriceToSalesRatioTTM") BigDecimal priceToSalesRatioTTM,
        @JsonProperty("PriceToBookRatio") BigDecimal priceToBookRatio,
        @JsonProperty("EVToRevenue") BigDecimal evToRevenue,
        @JsonProperty("EVToEBITDA") BigDecimal evToEBITDA,
        @JsonProperty("Beta") BigDecimal beta,
        @JsonProperty("52WeekHigh") BigDecimal weekHigh52,
        @JsonProperty("52WeekLow") BigDecimal weekLow52,
        @JsonProperty("50DayMovingAverage") BigDecimal movingAverage50Day,
        @JsonProperty("200DayMovingAverage") BigDecimal movingAverage200Day,
        @JsonProperty("SharesOutstanding") BigDecimal sharesOutstanding,
        @JsonProperty("FiscalYearEnd") String fiscalYearEnd
) {}
