package com.stock.screener.adapter.web.out.yhfinance;

import com.stock.screener.adapter.web.out.yhfinance.clients.*;
import com.stock.screener.adapter.web.out.yhfinance.enums.ChartInterval;
import com.stock.screener.adapter.web.out.yhfinance.enums.ChartRange;
import com.stock.screener.adapter.web.out.yhfinance.enums.QuoteSummaryModule;
import com.stock.screener.adapter.web.out.yhfinance.enums.ScreenerId;
import com.stock.screener.adapter.web.out.model.autocomplete.AutocompleteResponse;
import com.stock.screener.adapter.web.out.model.chart.ChartResponse;
import com.stock.screener.adapter.web.out.model.insights.InsightsResponse;
import com.stock.screener.adapter.web.out.model.marketsummary.MarketSummaryResponse;
import com.stock.screener.adapter.web.out.model.options.OptionChainResponse;
import com.stock.screener.adapter.web.out.model.quote.Quote;
import com.stock.screener.adapter.web.out.model.quote.QuoteResponse;
import com.stock.screener.adapter.web.out.model.quotesummary.QuoteSummaryResponse;
import com.stock.screener.adapter.web.out.model.recommendation.RecommendationResponse;
import com.stock.screener.adapter.web.out.model.screener.ScreenerResponse;
import com.stock.screener.adapter.web.out.model.spark.SparkResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fasada dla YH Finance API agregujaca wszystkie klienty REST.
 */
@Slf4j
@ApplicationScoped
public class YhFinanceApiFacade {

    private static final int MAX_SYMBOLS = 10;
    private static final String DEFAULT_REGION = "US";
    private static final String DEFAULT_LANG = "en";

    private final YhFinanceQuoteClient quoteClient;
    private final YhFinanceQuoteSummaryClient quoteSummaryClient;
    private final YhFinanceChartClient chartClient;
    private final YhFinanceScreenerClient screenerClient;
    private final YhFinanceInsightsClient insightsClient;
    private final YhFinanceAutocompleteClient autocompleteClient;
    private final YhFinanceRecommendationClient recommendationClient;
    private final YhFinanceMarketSummaryClient marketSummaryClient;
    private final YhFinanceOptionsClient optionsClient;
    private final YhFinanceSparkClient sparkClient;

    public YhFinanceApiFacade(
            @RestClient YhFinanceQuoteClient quoteClient,
            @RestClient YhFinanceQuoteSummaryClient quoteSummaryClient,
            @RestClient YhFinanceChartClient chartClient,
            @RestClient YhFinanceScreenerClient screenerClient,
            @RestClient YhFinanceInsightsClient insightsClient,
            @RestClient YhFinanceAutocompleteClient autocompleteClient,
            @RestClient YhFinanceRecommendationClient recommendationClient,
            @RestClient YhFinanceMarketSummaryClient marketSummaryClient,
            @RestClient YhFinanceOptionsClient optionsClient,
            @RestClient YhFinanceSparkClient sparkClient
    ) {
        this.quoteClient = quoteClient;
        this.quoteSummaryClient = quoteSummaryClient;
        this.chartClient = chartClient;
        this.screenerClient = screenerClient;
        this.insightsClient = insightsClient;
        this.autocompleteClient = autocompleteClient;
        this.recommendationClient = recommendationClient;
        this.marketSummaryClient = marketSummaryClient;
        this.optionsClient = optionsClient;
        this.sparkClient = sparkClient;
    }

    // ==================== QUOTE ====================

    public QuoteResponse getQuotes(List<String> symbols) {
        validateSymbols(symbols);
        String symbolsParam = String.join(",", symbols);
        log.debug("Fetching quotes for: {}", symbolsParam);
        return quoteClient.getQuotes(symbolsParam, DEFAULT_REGION, DEFAULT_LANG);
    }

    public Optional<Quote> getQuote(String symbol) {
        return getQuotes(List.of(symbol))
                .quoteResponse()
                .result()
                .stream()
                .findFirst();
    }

    // ==================== QUOTE SUMMARY ====================

    public QuoteSummaryResponse getQuoteSummary(String symbol, QuoteSummaryModule... modules) {
        validateSymbol(symbol);
        String modulesParam = Arrays.stream(modules)
                .map(QuoteSummaryModule::getValue)
                .collect(Collectors.joining(","));
        log.debug("Fetching quote summary for {} with modules: {}", symbol, modulesParam);
        return quoteSummaryClient.getQuoteSummary(symbol, modulesParam, DEFAULT_REGION, DEFAULT_LANG);
    }

    public QuoteSummaryResponse getFullFundamentals(String symbol) {
        return getQuoteSummary(symbol,
                QuoteSummaryModule.ASSET_PROFILE,
                QuoteSummaryModule.FINANCIAL_DATA,
                QuoteSummaryModule.DEFAULT_KEY_STATISTICS,
                QuoteSummaryModule.INCOME_STATEMENT_HISTORY,
                QuoteSummaryModule.BALANCE_SHEET_HISTORY,
                QuoteSummaryModule.CASHFLOW_STATEMENT_HISTORY,
                QuoteSummaryModule.EARNINGS
        );
    }

    // ==================== CHART ====================

    public ChartResponse getChart(String symbol, ChartInterval interval, ChartRange range) {
        validateSymbol(symbol);
        log.debug("Fetching chart for {}, interval={}, range={}", symbol, interval, range);
        return chartClient.getChart(symbol, interval.getValue(), range.getValue());
    }

    public ChartResponse getChart(String symbol) {
        return getChart(symbol, ChartInterval.ONE_DAY, ChartRange.ONE_MONTH);
    }

    // ==================== SCREENER ====================

    public ScreenerResponse getScreenerResults(ScreenerId screenerId, int count) {
        log.debug("Fetching screener: {}, count={}", screenerId, count);
        return screenerClient.getScreenerResults(screenerId.getValue(), count);
    }

    public ScreenerResponse getDayGainers(int count) {
        return getScreenerResults(ScreenerId.DAY_GAINERS, count);
    }

    public ScreenerResponse getDayLosers(int count) {
        return getScreenerResults(ScreenerId.DAY_LOSERS, count);
    }

    public ScreenerResponse getMostActives(int count) {
        return getScreenerResults(ScreenerId.MOST_ACTIVES, count);
    }

    // ==================== INSIGHTS ====================

    public InsightsResponse getInsights(String symbol) {
        validateSymbol(symbol);
        log.debug("Fetching insights for: {}", symbol);
        return insightsClient.getInsights(symbol);
    }

    // ==================== AUTOCOMPLETE ====================

    public AutocompleteResponse search(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        log.debug("Searching for: {}", query);
        return autocompleteClient.search(query, DEFAULT_REGION, DEFAULT_LANG);
    }

    // ==================== RECOMMENDATION ====================

    public RecommendationResponse getRecommendations(String symbol) {
        validateSymbol(symbol);
        log.debug("Fetching recommendations for: {}", symbol);
        return recommendationClient.getRecommendations(symbol);
    }

    // ==================== MARKET SUMMARY ====================

    public MarketSummaryResponse getMarketSummary() {
        log.debug("Fetching market summary");
        return marketSummaryClient.getMarketSummary(DEFAULT_REGION, DEFAULT_LANG);
    }

    // ==================== OPTIONS ====================

    public OptionChainResponse getOptions(String symbol) {
        validateSymbol(symbol);
        log.debug("Fetching options for: {}", symbol);
        return optionsClient.getOptions(symbol);
    }

    public OptionChainResponse getOptions(String symbol, Long expirationDate) {
        validateSymbol(symbol);
        log.debug("Fetching options for: {}, date={}", symbol, expirationDate);
        return optionsClient.getOptions(symbol, expirationDate);
    }

    // ==================== SPARK ====================

    public SparkResponse getSpark(List<String> symbols, ChartInterval interval, ChartRange range) {
        validateSymbols(symbols);
        String symbolsParam = String.join(",", symbols);
        log.debug("Fetching spark for {}, interval={}, range={}", symbolsParam, interval, range);
        return sparkClient.getSpark(symbolsParam, interval.getValue(), range.getValue());
    }

    // ==================== VALIDATION ====================

    private void validateSymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be empty");
        }
    }

    private void validateSymbols(List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            throw new IllegalArgumentException("Symbols list cannot be empty");
        }
        if (symbols.size() > MAX_SYMBOLS) {
            throw new IllegalArgumentException("Maximum " + MAX_SYMBOLS + " symbols allowed");
        }
        symbols.forEach(this::validateSymbol);
    }
}

