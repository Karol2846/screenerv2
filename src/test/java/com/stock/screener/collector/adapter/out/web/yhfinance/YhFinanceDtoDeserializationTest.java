package com.stock.screener.collector.adapter.out.web.yhfinance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.screener.collector.adapter.out.web.yhfinance.model.QuoteSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("YH Finance DTO Deserialization Tests")
class YhFinanceDtoDeserializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Pruned DTOs deserialize correctly with only required fields")
    void testMinimalDeserialization() throws Exception {
        // Given - minimal JSON with only the fields we need
        String json = """
                {
                    "quoteSummary": {
                        "result": [{
                            "earningsTrend": {
                                "trend": [{
                                    "period": "+1y",
                                    "growth": {"raw": 0.15, "fmt": "15.00%"},
                                    "revenueEstimate": {
                                        "growth": {"raw": 0.20, "fmt": "20.00%"}
                                    }
                                }]
                            },
                            "recommendationTrend": {
                                "trend": [{
                                    "period": "0m",
                                    "strongBuy": 10,
                                    "buy": 5,
                                    "hold": 8,
                                    "sell": 2,
                                    "strongSell": 1
                                }]
                            }
                        }],
                        "error": null
                    }
                }
                """;

        // When
        QuoteSummaryResponse response = objectMapper.readValue(json, QuoteSummaryResponse.class);

        // Then
        assertThat(response.quoteSummary().result()).hasSize(1);

        var result = response.quoteSummary().result().getFirst();
        assertThat(result.earningsTrend().trend().getFirst().growth().raw()).isEqualTo(0.15);
        assertThat(result.earningsTrend().trend().getFirst().revenueEstimate().growth().raw()).isEqualTo(0.20);
        assertThat(result.recommendationTrend().trend().getFirst().strongBuy()).isEqualTo(10);
        assertThat(result.recommendationTrend().trend().getFirst().buy()).isEqualTo(5);
        assertThat(result.recommendationTrend().trend().getFirst().hold()).isEqualTo(8);
        assertThat(result.recommendationTrend().trend().getFirst().sell()).isEqualTo(2);
        assertThat(result.recommendationTrend().trend().getFirst().strongSell()).isEqualTo(1);
    }

    @Test
    @DisplayName("Pruned DTOs ignore unknown fields from full API response")
    void testIgnoresUnknownFields() throws Exception {
        // Given - JSON with extra fields that no longer have corresponding DTO fields
        String json = """
                {
                    "quoteSummary": {
                        "result": [{
                            "assetProfile": {"sector": "Technology", "industry": "Software"},
                            "financialData": {"currentPrice": {"raw": 150.0}, "targetMeanPrice": {"raw": 180.0}},
                            "summaryDetail": {"marketCap": {"raw": 2500000000000}},
                            "earningsTrend": {
                                "trend": [{
                                    "maxAge": 1,
                                    "period": "+1y",
                                    "endDate": "2025-12-31",
                                    "growth": {"raw": 0.12, "fmt": "12.00%"},
                                    "earningsEstimate": {"avg": {"raw": 6.5}},
                                    "revenueEstimate": {
                                        "avg": {"raw": 400000000000},
                                        "growth": {"raw": 0.08, "fmt": "8.00%"},
                                        "revenueCurrency": "USD"
                                    },
                                    "epsTrend": {"current": {"raw": 6.2}},
                                    "epsRevisions": {"upLast7days": {"raw": 3}}
                                }],
                                "defaultMethodology": "normalized",
                                "maxAge": 1
                            },
                            "recommendationTrend": {
                                "trend": [{
                                    "period": "0m",
                                    "strongBuy": 15,
                                    "buy": 10,
                                    "hold": 5,
                                    "sell": 1,
                                    "strongSell": 0
                                }],
                                "maxAge": 86400
                            }
                        }],
                        "error": null
                    }
                }
                """;

        // When
        QuoteSummaryResponse response = objectMapper.readValue(json, QuoteSummaryResponse.class);

        // Then - only mapped fields are accessible
        var result = response.quoteSummary().result().getFirst();
        assertThat(result.earningsTrend().trend().getFirst().growth().raw()).isEqualTo(0.12);
        assertThat(result.earningsTrend().trend().getFirst().revenueEstimate().growth().raw()).isEqualTo(0.08);
        assertThat(result.recommendationTrend().trend().getFirst().strongBuy()).isEqualTo(15);
    }
}
