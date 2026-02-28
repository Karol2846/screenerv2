package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;

import java.math.BigDecimal;
import java.util.List;

public final class RawIncomeStatementFixture {

    private String symbol = "AAPL";
    private List<RawIncomeStatement.Report> annualReports = List.of();
    private List<RawIncomeStatement.Report> quarterlyReports = List.of(aReport().build());

    private RawIncomeStatementFixture() {}

    public static RawIncomeStatementFixture aRawIncomeStatement() {
        return new RawIncomeStatementFixture();
    }

    public RawIncomeStatementFixture withQuarterlyReports(List<RawIncomeStatement.Report> reports) {
        this.quarterlyReports = reports;
        return this;
    }

    public RawIncomeStatementFixture withQuarterlyReports(RawIncomeStatement.Report... reports) {
        this.quarterlyReports = List.of(reports);
        return this;
    }

    public RawIncomeStatementFixture withAnnualReports(List<RawIncomeStatement.Report> reports) {
        this.annualReports = reports;
        return this;
    }

    public RawIncomeStatementFixture withNullQuarterlyReports() {
        this.quarterlyReports = null;
        return this;
    }

    public RawIncomeStatement build() {
        return RawIncomeStatement.builder()
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
        private BigDecimal grossProfit = new BigDecimal("180683000000");
        private BigDecimal totalRevenue = new BigDecimal("391035000000");
        private BigDecimal costOfRevenue = new BigDecimal("210352000000");
        private BigDecimal costOfGoodsAndServicesSold = new BigDecimal("210352000000");
        private BigDecimal operatingIncome = new BigDecimal("123216000000");
        private BigDecimal sellingGeneralAndAdministrative = new BigDecimal("26097000000");
        private BigDecimal researchAndDevelopment = new BigDecimal("31370000000");
        private BigDecimal operatingExpenses = new BigDecimal("267819000000");
        private BigDecimal netIncome = new BigDecimal("93736000000");
        private BigDecimal ebit = new BigDecimal("123216000000");
        private BigDecimal ebitda = new BigDecimal("134642000000");
        private BigDecimal depreciationAndAmortization = new BigDecimal("11426000000");
        private BigDecimal interestIncome = new BigDecimal("3034000000");
        private BigDecimal interestExpense = new BigDecimal("3200000000");
        private BigDecimal incomeTaxExpense = new BigDecimal("29749000000");
        private BigDecimal incomeBeforeTax = new BigDecimal("123485000000");
        private BigDecimal netIncomeFromContinuingOperations = new BigDecimal("93736000000");

        private ReportFixture() {}

        public ReportFixture withTotalRevenue(String value) {
            this.totalRevenue = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullTotalRevenue() {
            this.totalRevenue = null;
            return this;
        }

        public ReportFixture withNetIncome(String value) {
            this.netIncome = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullNetIncome() {
            this.netIncome = null;
            return this;
        }

        public ReportFixture withEbit(String value) {
            this.ebit = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullEbit() {
            this.ebit = null;
            return this;
        }

        public ReportFixture withInterestExpense(String value) {
            this.interestExpense = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullInterestExpense() {
            this.interestExpense = null;
            return this;
        }

        public ReportFixture withIncomeTaxExpense(String value) {
            this.incomeTaxExpense = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullIncomeTaxExpense() {
            this.incomeTaxExpense = null;
            return this;
        }

        public RawIncomeStatement.Report build() {
            return RawIncomeStatement.Report.builder()
                    .fiscalDateEnding(fiscalDateEnding)
                    .reportedCurrency(reportedCurrency)
                    .grossProfit(grossProfit)
                    .totalRevenue(totalRevenue)
                    .costOfRevenue(costOfRevenue)
                    .costOfGoodsAndServicesSold(costOfGoodsAndServicesSold)
                    .operatingIncome(operatingIncome)
                    .sellingGeneralAndAdministrative(sellingGeneralAndAdministrative)
                    .researchAndDevelopment(researchAndDevelopment)
                    .operatingExpenses(operatingExpenses)
                    .netIncome(netIncome)
                    .ebit(ebit)
                    .ebitda(ebitda)
                    .depreciationAndAmortization(depreciationAndAmortization)
                    .interestIncome(interestIncome)
                    .interestExpense(interestExpense)
                    .incomeTaxExpense(incomeTaxExpense)
                    .incomeBeforeTax(incomeBeforeTax)
                    .netIncomeFromContinuingOperations(netIncomeFromContinuingOperations)
                    .build();
        }
    }
}
