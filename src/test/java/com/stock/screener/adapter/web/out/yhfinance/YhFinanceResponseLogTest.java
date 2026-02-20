package com.stock.screener.adapter.web.out.yhfinance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("YhFinanceResponseLog Entity Tests")
class YhFinanceResponseLogTest {

    @Test
    @DisplayName("Constructor sets all fields correctly")
    void testConstructorSetsFields() {
        // Given
        String ticker = "AAPL";
        String rawResponse = "{\"quoteSummary\":{\"result\":[{}]}}";

        // When
        var log = new YhFinanceResponseLog(ticker, rawResponse);

        // Then
        assertThat(log.ticker).isEqualTo(ticker);
        assertThat(log.rawResponse).isEqualTo(rawResponse);
    }

    @Test
    @DisplayName("Default constructor creates empty entity")
    void testDefaultConstructor() {
        // When
        var log = new YhFinanceResponseLog();

        // Then
        assertThat(log.ticker).isNull();
        assertThat(log.rawResponse).isNull();
        assertThat(log.requestTimestamp).isNull();
    }

    @Test
    @DisplayName("rawResponse stores valid JSON string")
    void testRawResponseStoresJson() {
        // Given
        String complexJson = """
                {
                    "quoteSummary": {
                        "result": [
                            {"earningsTrend": {"trend": []}, "recommendationTrend": {"trend": []}}
                        ]
                    }
                }
                """;

        // When
        var log = new YhFinanceResponseLog("MSFT", complexJson);

        // Then
        assertThat(log.ticker).isEqualTo("MSFT");
        assertThat(log.rawResponse).contains("earningsTrend");
    }
}
