package com.stock.screener.adapter.web.out.alphavantage;

import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetReport;
import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementReport;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.OverviewResponse;
import com.stock.screener.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.application.port.out.alphavantage.RawOverview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlphaVantageResponseMapper Tests")
class AlphaVantageResponseMapperTest {

    @Nested
    @DisplayName("toRawOverview")
    class ToRawOverviewTests {

        @Test
        @DisplayName("Maps all fields from OverviewResponse to RawOverview")
        void mapsAllFields() {
            var response = new OverviewResponse(
                    "AAPL", "Common Stock", "Apple Inc", "NASDAQ", "USD", "USA",
                    "TECHNOLOGY", "ELECTRONIC COMPUTERS",
                    new BigDecimal("2800000000000"), new BigDecimal("130000000000"),
                    new BigDecimal("28.50"), new BigDecimal("2.10"),
                    new BigDecimal("4.25"), new BigDecimal("0.82"),
                    new BigDecimal("0.0055"), new BigDecimal("6.15"),
                    new BigDecimal("24.50"), new BigDecimal("0.265"),
                    new BigDecimal("0.305"), new BigDecimal("0.210"),
                    new BigDecimal("1.56"), new BigDecimal("380000000000"),
                    new BigDecimal("170000000000"),
                    new BigDecimal("0.08"), new BigDecimal("0.05"),
                    new BigDecimal("195.00"),
                    12, 20, 8, 2, 0,
                    new BigDecimal("27.00"), new BigDecimal("26.80"),
                    new BigDecimal("7.50"), new BigDecimal("45.00"),
                    new BigDecimal("6.80"), new BigDecimal("22.00"),
                    new BigDecimal("1.25"),
                    new BigDecimal("199.62"), new BigDecimal("164.08"),
                    new BigDecimal("185.00"), new BigDecimal("175.00"),
                    new BigDecimal("15500000000"),
                    "September"
            );

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
            var response = new OverviewResponse(
                    "AAPL", null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null
            );

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
            var report = new BalanceSheetReport(
                    "2024-09-30", "USD",
                    new BigDecimal("364980000000"), new BigDecimal("152987000000"),
                    new BigDecimal("212000000000"), new BigDecimal("308030000000"),
                    new BigDecimal("176392000000"), new BigDecimal("131638000000"),
                    new BigDecimal("56950000000"), new BigDecimal("4336000000"),
                    new BigDecimal("83276000000"), new BigDecimal("29943000000"),
                    new BigDecimal("65171000000"), new BigDecimal("7286000000"),
                    new BigDecimal("66243000000"), new BigDecimal("20232000000"),
                    new BigDecimal("96304000000"), new BigDecimal("10912000000"),
                    new BigDecimal("85392000000"), new BigDecimal("116536000000"),
                    new BigDecimal("15115000000")
            );
            var response = new BalanceSheetResponse("AAPL", List.of(report), List.of());

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
            var report = new IncomeStatementReport(
                    "2024-09-30", "USD",
                    new BigDecimal("180683000000"), new BigDecimal("391035000000"),
                    new BigDecimal("210352000000"), new BigDecimal("210352000000"),
                    new BigDecimal("123216000000"), new BigDecimal("26757000000"),
                    new BigDecimal("30710000000"), new BigDecimal("57467000000"),
                    new BigDecimal("93736000000"), new BigDecimal("123216000000"),
                    new BigDecimal("134642000000"), new BigDecimal("11426000000"),
                    new BigDecimal("3999000000"), new BigDecimal("3200000000"),
                    new BigDecimal("29749000000"), new BigDecimal("123485000000"),
                    new BigDecimal("93736000000")
            );
            var response = new IncomeStatementResponse("AAPL", List.of(report), List.of());

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
