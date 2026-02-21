package com.stock.screener.collector.adapter.out.web.alphavantage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlphaVantageResponseLog Entity Tests")
class AlphaVantageResponseLogTest {

    @Test
    @DisplayName("Constructor sets all fields correctly")
    void testConstructorSetsFields() {
        // Given
        String ticker = "AAPL";
        String functionName = "OVERVIEW";
        String rawResponse = "{\"Symbol\":\"AAPL\",\"Name\":\"Apple Inc\"}";

        // When
        var log = new AlphaVantageResponseLog(ticker, functionName, rawResponse);

        // Then
        assertThat(log.ticker).isEqualTo(ticker);
        assertThat(log.functionName).isEqualTo(functionName);
        assertThat(log.rawResponse).isEqualTo(rawResponse);
    }

    @Test
    @DisplayName("Default constructor creates empty entity")
    void testDefaultConstructor() {
        // When
        var log = new AlphaVantageResponseLog();

        // Then
        assertThat(log.ticker).isNull();
        assertThat(log.functionName).isNull();
        assertThat(log.rawResponse).isNull();
        assertThat(log.requestTimestamp).isNull();
    }

    @Test
    @DisplayName("rawResponse stores valid JSON string")
    void testRawResponseStoresJson() {
        // Given
        String complexJson = """
                {
                    "symbol": "MSFT",
                    "annualReports": [
                        {"fiscalDateEnding": "2024-06-30", "totalAssets": "512163000000"}
                    ]
                }
                """;

        // When
        var log = new AlphaVantageResponseLog("MSFT", "BALANCE_SHEET", complexJson);

        // Then
        assertThat(log.rawResponse).contains("MSFT");
        assertThat(log.rawResponse).contains("512163000000");
    }
}
