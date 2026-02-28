package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;

import java.math.BigDecimal;
import java.util.List;

public final class RawBalanceSheetFixture {

    // Defaults aligned with integrationTest/resources/stubs/balance_sheet.json (META)
    private String symbol = "META";
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

        // Defaults aligned with first quarterly report from integrationTest/resources/stubs/balance_sheet.json (META)
        private String fiscalDateEnding = "2025-12-31";
        private String reportedCurrency = "USD";
        private BigDecimal totalAssets = new BigDecimal("366021000000");
        private BigDecimal totalCurrentAssets = new BigDecimal("108722000000");
        private BigDecimal totalNonCurrentAssets = new BigDecimal("257299000000");
        private BigDecimal totalLiabilities = new BigDecimal("148778000000");
        private BigDecimal totalCurrentLiabilities = new BigDecimal("41836000000");
        private BigDecimal totalNonCurrentLiabilities = new BigDecimal("106942000000");
        private BigDecimal totalShareholderEquity = new BigDecimal("217243000000");
        private BigDecimal retainedEarnings = new BigDecimal("121179000000");
        private BigDecimal commonStock = null;
        private BigDecimal cashAndCashEquivalents = new BigDecimal("35873000000");
        private BigDecimal cashAndShortTermInvestments = new BigDecimal("35873000000");
        private BigDecimal inventory = null;
        private BigDecimal currentNetReceivables = new BigDecimal("19769000000");
        private BigDecimal shortTermDebt = new BigDecimal("2213000000");
        private BigDecimal longTermDebt = new BigDecimal("58744000000");
        private BigDecimal currentLongTermDebt = null;
        private BigDecimal longTermDebtNoncurrent = null;
        private BigDecimal shortLongTermDebtTotal = new BigDecimal("83897000000");
        private BigDecimal commonStockSharesOutstanding = new BigDecimal("2574000000");
        private BigDecimal additionalPaidInCapital = null;

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
