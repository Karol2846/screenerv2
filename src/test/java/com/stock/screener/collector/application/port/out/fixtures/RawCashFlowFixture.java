package com.stock.screener.collector.application.port.out.fixtures;

import com.stock.screener.collector.application.port.out.alphavantage.RawCashFlow;

import java.math.BigDecimal;
import java.util.List;

public final class RawCashFlowFixture {

    private String symbol = "META";
    private List<RawCashFlow.Report> annualReports = List.of();
    private List<RawCashFlow.Report> quarterlyReports = List.of(aReport().build());

    private RawCashFlowFixture() {}

    public static RawCashFlowFixture aRawCashFlow() {
        return new RawCashFlowFixture();
    }

    public RawCashFlowFixture withQuarterlyReports(List<RawCashFlow.Report> reports) {
        this.quarterlyReports = reports;
        return this;
    }

    public RawCashFlowFixture withQuarterlyReports(RawCashFlow.Report... reports) {
        this.quarterlyReports = List.of(reports);
        return this;
    }

    public RawCashFlowFixture withAnnualReports(List<RawCashFlow.Report> reports) {
        this.annualReports = reports;
        return this;
    }

    public RawCashFlowFixture withNullQuarterlyReports() {
        this.quarterlyReports = null;
        return this;
    }

    public RawCashFlow build() {
        return RawCashFlow.builder()
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

        private String fiscalDateEnding = "2025-12-31";
        private String reportedCurrency = "USD";
        private BigDecimal operatingCashflow = new BigDecimal("36214000000");
        private BigDecimal paymentsForOperatingActivities = null;
        private BigDecimal proceedsFromOperatingActivities = null;
        private BigDecimal changeInOperatingLiabilities = null;
        private BigDecimal changeInOperatingAssets = null;
        private BigDecimal depreciationDepletionAndAmortization = new BigDecimal("5411000000");
        private BigDecimal capitalExpenditures = new BigDecimal("21383000000");
        private BigDecimal changeInReceivables = null;
        private BigDecimal changeInInventory = null;
        private BigDecimal profitLoss = null;
        private BigDecimal cashflowFromInvestment = new BigDecimal("-34187000000");
        private BigDecimal cashflowFromFinancing = new BigDecimal("25149000000");
        private BigDecimal proceedsFromRepaymentsOfShortTermDebt = null;
        private BigDecimal paymentsForRepurchaseOfCommonStock = null;
        private BigDecimal paymentsForRepurchaseOfEquity = null;
        private BigDecimal paymentsForRepurchaseOfPreferredStock = null;
        private BigDecimal dividendPayout = new BigDecimal("1338000000");
        private BigDecimal dividendPayoutCommonStock = new BigDecimal("1338000000");
        private BigDecimal dividendPayoutPreferredStock = null;
        private BigDecimal proceedsFromIssuanceOfCommonStock = null;
        private BigDecimal proceedsFromIssuanceOfLongTermDebtAndCapitalSecuritiesNet = null;
        private BigDecimal proceedsFromIssuanceOfPreferredStock = null;
        private BigDecimal proceedsFromRepurchaseOfEquity = new BigDecimal("26248000000");
        private BigDecimal proceedsFromSaleOfTreasuryStock = null;
        private BigDecimal changeInCashAndCashEquivalents = null;
        private BigDecimal changeInExchangeRate = null;
        private BigDecimal netIncome = new BigDecimal("22768000000");

        private ReportFixture() {}

        public ReportFixture withOperatingCashflow(String value) {
            this.operatingCashflow = new BigDecimal(value);
            return this;
        }

        public ReportFixture withNullOperatingCashflow() {
            this.operatingCashflow = null;
            return this;
        }

        public ReportFixture withNetIncome(String value) {
            this.netIncome = new BigDecimal(value);
            return this;
        }

        public RawCashFlow.Report build() {
            return RawCashFlow.Report.builder()
                    .fiscalDateEnding(fiscalDateEnding)
                    .reportedCurrency(reportedCurrency)
                    .operatingCashflow(operatingCashflow)
                    .paymentsForOperatingActivities(paymentsForOperatingActivities)
                    .proceedsFromOperatingActivities(proceedsFromOperatingActivities)
                    .changeInOperatingLiabilities(changeInOperatingLiabilities)
                    .changeInOperatingAssets(changeInOperatingAssets)
                    .depreciationDepletionAndAmortization(depreciationDepletionAndAmortization)
                    .capitalExpenditures(capitalExpenditures)
                    .changeInReceivables(changeInReceivables)
                    .changeInInventory(changeInInventory)
                    .profitLoss(profitLoss)
                    .cashflowFromInvestment(cashflowFromInvestment)
                    .cashflowFromFinancing(cashflowFromFinancing)
                    .proceedsFromRepaymentsOfShortTermDebt(proceedsFromRepaymentsOfShortTermDebt)
                    .paymentsForRepurchaseOfCommonStock(paymentsForRepurchaseOfCommonStock)
                    .paymentsForRepurchaseOfEquity(paymentsForRepurchaseOfEquity)
                    .paymentsForRepurchaseOfPreferredStock(paymentsForRepurchaseOfPreferredStock)
                    .dividendPayout(dividendPayout)
                    .dividendPayoutCommonStock(dividendPayoutCommonStock)
                    .dividendPayoutPreferredStock(dividendPayoutPreferredStock)
                    .proceedsFromIssuanceOfCommonStock(proceedsFromIssuanceOfCommonStock)
                    .proceedsFromIssuanceOfLongTermDebtAndCapitalSecuritiesNet(proceedsFromIssuanceOfLongTermDebtAndCapitalSecuritiesNet)
                    .proceedsFromIssuanceOfPreferredStock(proceedsFromIssuanceOfPreferredStock)
                    .proceedsFromRepurchaseOfEquity(proceedsFromRepurchaseOfEquity)
                    .proceedsFromSaleOfTreasuryStock(proceedsFromSaleOfTreasuryStock)
                    .changeInCashAndCashEquivalents(changeInCashAndCashEquivalents)
                    .changeInExchangeRate(changeInExchangeRate)
                    .netIncome(netIncome)
                    .build();
        }
    }
}
