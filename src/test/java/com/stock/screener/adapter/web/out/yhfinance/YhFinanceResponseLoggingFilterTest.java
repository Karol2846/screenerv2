package com.stock.screener.adapter.web.out.yhfinance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("YhFinanceResponseLoggingFilter Tests")
class YhFinanceResponseLoggingFilterTest {

    @Test
    @DisplayName("extractTicker extracts ticker from URI path")
    void testExtractTicker() {
        // Given
        URI uri = URI.create("https://yfapi.net/v11/finance/quoteSummary/AAPL?modules=earningsTrend");

        // When
        String ticker = YhFinanceResponseLoggingFilter.extractTicker(uri);

        // Then
        assertThat(ticker).isEqualTo("AAPL");
    }

    @Test
    @DisplayName("extractTicker works with complex ticker symbols")
    void testExtractTickerComplex() {
        // Given
        URI uri = URI.create("https://yfapi.net/v11/finance/quoteSummary/BRK-B?modules=earningsTrend");

        // When
        String ticker = YhFinanceResponseLoggingFilter.extractTicker(uri);

        // Then
        assertThat(ticker).isEqualTo("BRK-B");
    }

    @Test
    @DisplayName("extractTicker works with path without query params")
    void testExtractTickerNoQueryParams() {
        // Given
        URI uri = URI.create("https://yfapi.net/v11/finance/quoteSummary/MSFT");

        // When
        String ticker = YhFinanceResponseLoggingFilter.extractTicker(uri);

        // Then
        assertThat(ticker).isEqualTo("MSFT");
    }
}
