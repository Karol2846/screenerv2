package com.stock.screener.adapter.web.out.alphavantage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.OverviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Alpha Vantage DTO Deserialization Tests")
class AlphaVantageDtoDeserializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("OverviewResponse")
    class OverviewResponseTests {

        @Test
        @DisplayName("Deserializes overview JSON with PascalCase field names")
        void testDeserializeOverview() throws Exception {
            // Given
            String json = """
                    {
                        "Symbol": "AAPL",
                        "AssetType": "Common Stock",
                        "Name": "Apple Inc",
                        "Exchange": "NASDAQ",
                        "Currency": "USD",
                        "Country": "USA",
                        "Sector": "TECHNOLOGY",
                        "Industry": "ELECTRONIC COMPUTERS",
                        "MarketCapitalization": "2800000000000",
                        "EBITDA": "130000000000",
                        "PERatio": "28.50",
                        "PEGRatio": "2.10",
                        "BookValue": "4.25",
                        "EPS": "6.15",
                        "RevenueTTM": "380000000000",
                        "ProfitMargin": "0.265",
                        "ForwardPE": "26.80",
                        "Beta": "1.25",
                        "52WeekHigh": "199.62",
                        "52WeekLow": "164.08",
                        "SharesOutstanding": "15500000000",
                        "AnalystTargetPrice": "195.00",
                        "AnalystRatingStrongBuy": 12,
                        "AnalystRatingBuy": 20,
                        "AnalystRatingHold": 8,
                        "AnalystRatingSell": 2,
                        "AnalystRatingStrongSell": 0
                    }
                    """;

            // When
            OverviewResponse response = objectMapper.readValue(json, OverviewResponse.class);

            // Then
            assertThat(response.symbol()).isEqualTo("AAPL");
            assertThat(response.name()).isEqualTo("Apple Inc");
            assertThat(response.sector()).isEqualTo("TECHNOLOGY");
            assertThat(response.marketCapitalization()).isEqualByComparingTo(new BigDecimal("2800000000000"));
            assertThat(response.peRatio()).isEqualByComparingTo(new BigDecimal("28.50"));
            assertThat(response.forwardPE()).isEqualByComparingTo(new BigDecimal("26.80"));
            assertThat(response.beta()).isEqualByComparingTo(new BigDecimal("1.25"));
            assertThat(response.weekHigh52()).isEqualByComparingTo(new BigDecimal("199.62"));
            assertThat(response.analystRatingStrongBuy()).isEqualTo(12);
            assertThat(response.analystRatingBuy()).isEqualTo(20);
        }

        @Test
        @DisplayName("Unknown JSON properties are ignored")
        void testIgnoresUnknownProperties() throws Exception {
            // Given
            String json = """
                    {
                        "Symbol": "AAPL",
                        "UnknownField": "should be ignored",
                        "AnotherUnknown": 12345
                    }
                    """;

            // When
            OverviewResponse response = objectMapper.readValue(json, OverviewResponse.class);

            // Then
            assertThat(response.symbol()).isEqualTo("AAPL");
        }
    }

    @Nested
    @DisplayName("BalanceSheetResponse")
    class BalanceSheetResponseTests {

        @Test
        @DisplayName("Deserializes balance sheet with quarterly reports")
        void testDeserializeBalanceSheet() throws Exception {
            // Given
            String json = """
                    {
                        "symbol": "AAPL",
                        "annualReports": [
                            {
                                "fiscalDateEnding": "2024-09-30",
                                "reportedCurrency": "USD",
                                "totalAssets": "364980000000",
                                "totalCurrentAssets": "152987000000",
                                "totalLiabilities": "308030000000",
                                "totalCurrentLiabilities": "176392000000",
                                "totalShareholderEquity": "56950000000",
                                "retainedEarnings": "4336000000",
                                "inventory": "7286000000",
                                "shortTermDebt": "20232000000",
                                "longTermDebt": "96304000000"
                            }
                        ],
                        "quarterlyReports": [
                            {
                                "fiscalDateEnding": "2024-12-31",
                                "reportedCurrency": "USD",
                                "totalAssets": "400000000000",
                                "totalCurrentAssets": "160000000000",
                                "totalLiabilities": "310000000000",
                                "totalCurrentLiabilities": "180000000000"
                            }
                        ]
                    }
                    """;

            // When
            BalanceSheetResponse response = objectMapper.readValue(json, BalanceSheetResponse.class);

            // Then
            assertThat(response.symbol()).isEqualTo("AAPL");
            assertThat(response.annualReports()).hasSize(1);
            assertThat(response.quarterlyReports()).hasSize(1);

            var annual = response.annualReports().getFirst();
            assertThat(annual.fiscalDateEnding()).isEqualTo("2024-09-30");
            assertThat(annual.totalAssets()).isEqualByComparingTo(new BigDecimal("364980000000"));
            assertThat(annual.inventory()).isEqualByComparingTo(new BigDecimal("7286000000"));
            assertThat(annual.longTermDebt()).isEqualByComparingTo(new BigDecimal("96304000000"));
        }
    }

    @Nested
    @DisplayName("IncomeStatementResponse")
    class IncomeStatementResponseTests {

        @Test
        @DisplayName("Deserializes income statement with quarterly reports")
        void testDeserializeIncomeStatement() throws Exception {
            // Given
            String json = """
                    {
                        "symbol": "AAPL",
                        "annualReports": [
                            {
                                "fiscalDateEnding": "2024-09-30",
                                "reportedCurrency": "USD",
                                "totalRevenue": "391035000000",
                                "grossProfit": "180683000000",
                                "operatingIncome": "123216000000",
                                "netIncome": "93736000000",
                                "ebit": "123216000000",
                                "ebitda": "134642000000",
                                "interestExpense": "3200000000",
                                "interestIncome": "3999000000"
                            }
                        ],
                        "quarterlyReports": [
                            {
                                "fiscalDateEnding": "2024-12-31",
                                "reportedCurrency": "USD",
                                "totalRevenue": "124300000000",
                                "netIncome": "36330000000"
                            }
                        ]
                    }
                    """;

            // When
            IncomeStatementResponse response = objectMapper.readValue(json, IncomeStatementResponse.class);

            // Then
            assertThat(response.symbol()).isEqualTo("AAPL");
            assertThat(response.annualReports()).hasSize(1);
            assertThat(response.quarterlyReports()).hasSize(1);

            var annual = response.annualReports().getFirst();
            assertThat(annual.fiscalDateEnding()).isEqualTo("2024-09-30");
            assertThat(annual.totalRevenue()).isEqualByComparingTo(new BigDecimal("391035000000"));
            assertThat(annual.netIncome()).isEqualByComparingTo(new BigDecimal("93736000000"));
            assertThat(annual.ebitda()).isEqualByComparingTo(new BigDecimal("134642000000"));
            assertThat(annual.interestExpense()).isEqualByComparingTo(new BigDecimal("3200000000"));
        }
    }
}
