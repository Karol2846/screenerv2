package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;

import java.math.BigDecimal;
import java.util.List;

public final class RawBalanceSheetFixture {

    private String symbol = "AAPL";
    private List<RawBalanceSheet.Report> annualReports = List.of();
    private List<RawBalanceSheet.Report> quarterlyReports = List.of(aReport().build());

    private RawBalanceSheetFixture() {}

    public static RawBalanceSheetFixture aRawBalanceSheet() {
        return new RawBalanceSheetFixture();
    }

    public RawBalanceSheetFixture withQuarterlyReports(List<RawBalanceSheet.Report> reports) {
        this.quarterlyReports = reports;
        return this;
    }

    public RawBalanceSheetFixture withQuarterlyReports(RawBalanceSheet.Report... reports) {
        this.quarterlyReports = List.of(reports);
        return this;
    }

    public RawBalanceSheetFixture withAnnualReports(List<RawBalanceSheet.Report> reports) {
        this.annualReports = reports;
        return this;
    }

    public RawBalanceSheetFixture withNullQuarterlyReports() {
        this.quarterlyReports = null;
        return this;
    }

    public RawBalanceSheet build() {
        return RawBalanceSheet.builder()
                .symbol(symbol)
                .annualReports(annualReports)
                .quarterlyReports(quarterlyReports)
                .build();
    }

    // --- Report builder ---

    public static ReportFixture aReport() {
        return new ReportFixture();
    }

    public static final class ReportFixture {

        private String fiscalDateEnding = "2024-09-30";
        private String reportedCurrency = "USD";
        private BigDecimal totalAssets = new BigDecimal("364980000000");
        private BigDecimal totalCurrentAssets = new BigDecimal("152987000000");
        private BigDecimal totalNonCurrentAssets = new BigDecimal("211993000000");
        private BigDecimal totalLiabilities = new BigDecimal("308030000000");
        private BigDecimal totalCurrentLiabilities = new BigDecimal("176392000000");
        private BigDecimal totalNonCurrentLiabilities = new BigDecimal("131638000000");
        private BigDecimal totalShareholderEquity = new BigDecimal("56950000000");
        private BigDecimal retainedEarnings = new BigDecimal("4336000000");
        private BigDecimal commonStock = new BigDecimal("83276000000");
        private BigDecimal cashAndCashEquivalents = new BigDecimal("29943000000");
        private BigDecimal cashAndShortTermInvestments = new BigDecimal("65171000000");
        private BigDecimal inventory = new BigDecimal("7286000000");
        private BigDecimal currentNetReceivables = new BigDecimal("66243000000");
        private BigDecimal shortTermDebt = new BigDecimal("22511000000");
        private BigDecimal longTermDebt = new BigDecimal("96304000000");
        private BigDecimal currentLongTermDebt = new BigDecimal("10912000000");
        private BigDecimal longTermDebtNoncurrent = new BigDecimal("96304000000");
        private BigDecimal shortLongTermDebtTotal = new BigDecimal("118815000000");
        private BigDecimal commonStockSharesOutstanding = new BigDecimal("15408095000");
        private BigDecimal additionalPaidInCapital = new BigDecimal("0");

        private ReportFixture() {}

        public ReportFixture withTotalAssets(String value) {
            this.totalAssets = new BigDecimal(value);
            return this;
        }

        public ReportFixture withTotalCurrentAssets(String value) {
            this.totalCurrentAssets = new BigDecimal(value);
            return this;
        }

        public ReportFixture withTotalCurrentLiabilities(String value) {
            this.totalCurrentLiabilities = new BigDecimal(value);
            return this;
        }

        public ReportFixture withTotalLiabilities(String value) {
            this.totalLiabilities = new BigDecimal(value);
            return this;
        }

        public ReportFixture withTotalShareholderEquity(String value) {
            this.totalShareholderEquity = new BigDecimal(value);
            return this;
        }

        public ReportFixture withRetainedEarnings(String value) {
            this.retainedEarnings = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullRetainedEarnings() {
            this.retainedEarnings = null;
            return this;
        }

        public ReportFixture withCommonStock(String value) {
            this.commonStock = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullCommonStock() {
            this.commonStock = null;
            return this;
        }

        public ReportFixture withAdditionalPaidInCapital(String value) {
            this.additionalPaidInCapital = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullAdditionalPaidInCapital() {
            this.additionalPaidInCapital = null;
            return this;
        }

        public ReportFixture withInventory(String value) {
            this.inventory = new BigDecimal(value);
            return this;
        }

        public ReportFixture withShortLongTermDebtTotal(String value) {
            this.shortLongTermDebtTotal = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullShortLongTermDebtTotal() {
            this.shortLongTermDebtTotal = null;
            return this;
        }

        public ReportFixture withShortTermDebt(String value) {
            this.shortTermDebt = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullShortTermDebt() {
            this.shortTermDebt = null;
            return this;
        }

        public ReportFixture withLongTermDebt(String value) {
            this.longTermDebt = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullLongTermDebt() {
            this.longTermDebt = null;
            return this;
        }

        public RawBalanceSheet.Report build() {
            return RawBalanceSheet.Report.builder()
                    .fiscalDateEnding(fiscalDateEnding)
                    .reportedCurrency(reportedCurrency)
                    .totalAssets(totalAssets)
                    .totalCurrentAssets(totalCurrentAssets)
                    .totalNonCurrentAssets(totalNonCurrentAssets)
                    .totalLiabilities(totalLiabilities)
                    .totalCurrentLiabilities(totalCurrentLiabilities)
                    .totalNonCurrentLiabilities(totalNonCurrentLiabilities)
                    .totalShareholderEquity(totalShareholderEquity)
                    .retainedEarnings(retainedEarnings)
                    .commonStock(commonStock)
                    .cashAndCashEquivalents(cashAndCashEquivalents)
                    .cashAndShortTermInvestments(cashAndShortTermInvestments)
                    .inventory(inventory)
                    .currentNetReceivables(currentNetReceivables)
                    .shortTermDebt(shortTermDebt)
                    .longTermDebt(longTermDebt)
                    .currentLongTermDebt(currentLongTermDebt)
                    .longTermDebtNoncurrent(longTermDebtNoncurrent)
                    .shortLongTermDebtTotal(shortLongTermDebtTotal)
                    .commonStockSharesOutstanding(commonStockSharesOutstanding)
                    .additionalPaidInCapital(additionalPaidInCapital)
                    .build();
        }
    }
}
