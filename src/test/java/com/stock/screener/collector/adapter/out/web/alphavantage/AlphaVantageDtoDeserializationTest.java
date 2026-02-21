package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.stock.screener.collector.adapter.out.web.alphavantage.fixture.JsonFixtureLoader;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.OverviewResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Alpha Vantage DTO Deserialization Tests")
class AlphaVantageDtoDeserializationTest {

    @Nested
    @DisplayName("OverviewResponse")
    class OverviewResponseTests {

        @Test
        @DisplayName("Deserializes overview JSON with PascalCase field names")
        void testDeserializeOverview() {
            OverviewResponse response = JsonFixtureLoader.load("overview-response.json", OverviewResponse.class);

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
        void testIgnoresUnknownProperties() {
            OverviewResponse response = JsonFixtureLoader.load("overview-response-nulls.json", OverviewResponse.class);

            assertThat(response.symbol()).isEqualTo("AAPL");
        }
    }

    @Nested
    @DisplayName("BalanceSheetResponse")
    class BalanceSheetResponseTests {

        @Test
        @DisplayName("Deserializes balance sheet with annual reports")
        void testDeserializeBalanceSheet() {
            BalanceSheetResponse response = JsonFixtureLoader.load("balance-sheet-response.json", BalanceSheetResponse.class);

            assertThat(response.symbol()).isEqualTo("AAPL");
            assertThat(response.annualReports()).hasSize(1);
            assertThat(response.quarterlyReports()).isEmpty();

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
        @DisplayName("Deserializes income statement with annual reports")
        void testDeserializeIncomeStatement() {
            IncomeStatementResponse response = JsonFixtureLoader.load("income-statement-response.json", IncomeStatementResponse.class);

            assertThat(response.symbol()).isEqualTo("AAPL");
            assertThat(response.annualReports()).hasSize(1);
            assertThat(response.quarterlyReports()).isEmpty();

            var annual = response.annualReports().getFirst();
            assertThat(annual.fiscalDateEnding()).isEqualTo("2024-09-30");
            assertThat(annual.totalRevenue()).isEqualByComparingTo(new BigDecimal("391035000000"));
            assertThat(annual.netIncome()).isEqualByComparingTo(new BigDecimal("93736000000"));
            assertThat(annual.ebitda()).isEqualByComparingTo(new BigDecimal("134642000000"));
            assertThat(annual.interestExpense()).isEqualByComparingTo(new BigDecimal("3200000000"));
        }
    }
}
