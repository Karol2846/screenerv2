package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.stock.screener.collector.adapter.out.web.alphavantage.fixture.JsonFixtureLoader;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.collector.adapter.out.web.alphavantage.model.OverviewResponse;
import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlphaVantageResponseMapper Tests")
class AlphaVantageResponseMapperTest {

    @Nested
    @DisplayName("toRawOverview")
    class ToRawOverviewTests {

        @Test
        @DisplayName("Maps all fields from OverviewResponse to RawOverview")
        void mapsAllFields() {
            var response = JsonFixtureLoader.load("overview-response.json", OverviewResponse.class);

            RawOverview result = AlphaVantageResponseMapper.toRawOverview(response);

            assertThat(result.symbol()).isEqualTo("AAPL");
            assertThat(result.name()).isEqualTo("Apple Inc");
            assertThat(result.marketCapitalization()).isEqualByComparingTo(new BigDecimal("2800000000000"));
            assertThat(result.peRatio()).isEqualByComparingTo(new BigDecimal("28.50"));
            assertThat(result.analystRatingStrongBuy()).isEqualTo(12);
            assertThat(result.beta()).isEqualByComparingTo(new BigDecimal("1.25"));
            assertThat(result.fiscalYearEnd()).isEqualTo("September");
        }

        @Test
        @DisplayName("Handles null fields gracefully")
        void handlesNullFields() {
            var response = JsonFixtureLoader.load("overview-response-nulls.json", OverviewResponse.class);

            RawOverview result = AlphaVantageResponseMapper.toRawOverview(response);

            assertThat(result.symbol()).isEqualTo("AAPL");
            assertThat(result.marketCapitalization()).isNull();
            assertThat(result.peRatio()).isNull();
        }
    }

    @Nested
    @DisplayName("toRawBalanceSheet")
    class ToRawBalanceSheetTests {

        @Test
        @DisplayName("Maps balance sheet with annual and quarterly reports")
        void mapsReports() {
            var response = JsonFixtureLoader.load("balance-sheet-response.json", BalanceSheetResponse.class);

            RawBalanceSheet result = AlphaVantageResponseMapper.toRawBalanceSheet(response);

            assertThat(result.symbol()).isEqualTo("AAPL");
            assertThat(result.annualReports()).hasSize(1);
            assertThat(result.quarterlyReports()).isEmpty();

            var mapped = result.annualReports().getFirst();
            assertThat(mapped.fiscalDateEnding()).isEqualTo("2024-09-30");
            assertThat(mapped.totalAssets()).isEqualByComparingTo(new BigDecimal("364980000000"));
            assertThat(mapped.longTermDebt()).isEqualByComparingTo(new BigDecimal("96304000000"));
        }

        @Test
        @DisplayName("Handles null report lists")
        void handlesNullLists() {
            var response = new BalanceSheetResponse("AAPL", null, null);

            RawBalanceSheet result = AlphaVantageResponseMapper.toRawBalanceSheet(response);

            assertThat(result.annualReports()).isEmpty();
            assertThat(result.quarterlyReports()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toRawIncomeStatement")
    class ToRawIncomeStatementTests {

        @Test
        @DisplayName("Maps income statement with annual and quarterly reports")
        void mapsReports() {
            var response = JsonFixtureLoader.load("income-statement-response.json", IncomeStatementResponse.class);

            RawIncomeStatement result = AlphaVantageResponseMapper.toRawIncomeStatement(response);

            assertThat(result.symbol()).isEqualTo("AAPL");
            assertThat(result.annualReports()).hasSize(1);
            assertThat(result.quarterlyReports()).isEmpty();

            var mapped = result.annualReports().getFirst();
            assertThat(mapped.fiscalDateEnding()).isEqualTo("2024-09-30");
            assertThat(mapped.totalRevenue()).isEqualByComparingTo(new BigDecimal("391035000000"));
            assertThat(mapped.netIncome()).isEqualByComparingTo(new BigDecimal("93736000000"));
            assertThat(mapped.ebitda()).isEqualByComparingTo(new BigDecimal("134642000000"));
        }

        @Test
        @DisplayName("Handles null report lists")
        void handlesNullLists() {
            var response = new IncomeStatementResponse("AAPL", null, null);

            RawIncomeStatement result = AlphaVantageResponseMapper.toRawIncomeStatement(response);

            assertThat(result.annualReports()).isEmpty();
            assertThat(result.quarterlyReports()).isEmpty();
        }
    }
}

