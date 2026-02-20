package com.stock.screener.adapter.web.out.alphavantage;

import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetReport;
import com.stock.screener.adapter.web.out.alphavantage.model.BalanceSheetResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementReport;
import com.stock.screener.adapter.web.out.alphavantage.model.IncomeStatementResponse;
import com.stock.screener.adapter.web.out.alphavantage.model.OverviewResponse;
import com.stock.screener.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.application.port.out.alphavantage.RawOverview;

import java.util.List;

final class AlphaVantageResponseMapper {

    private AlphaVantageResponseMapper() {}

    static RawOverview toRawOverview(OverviewResponse r) {
        return new RawOverview(
                r.symbol(),
                r.assetType(),
                r.name(),
                r.exchange(),
                r.currency(),
                r.country(),
                r.sector(),
                r.industry(),
                r.marketCapitalization(),
                r.ebitda(),
                r.peRatio(),
                r.pegRatio(),
                r.bookValue(),
                r.dividendPerShare(),
                r.dividendYield(),
                r.eps(),
                r.revenuePerShareTTM(),
                r.profitMargin(),
                r.operatingMarginTTM(),
                r.returnOnAssetsTTM(),
                r.returnOnEquityTTM(),
                r.revenueTTM(),
                r.grossProfitTTM(),
                r.quarterlyEarningsGrowthYOY(),
                r.quarterlyRevenueGrowthYOY(),
                r.analystTargetPrice(),
                r.analystRatingStrongBuy(),
                r.analystRatingBuy(),
                r.analystRatingHold(),
                r.analystRatingSell(),
                r.analystRatingStrongSell(),
                r.trailingPE(),
                r.forwardPE(),
                r.priceToSalesRatioTTM(),
                r.priceToBookRatio(),
                r.evToRevenue(),
                r.evToEBITDA(),
                r.beta(),
                r.weekHigh52(),
                r.weekLow52(),
                r.movingAverage50Day(),
                r.movingAverage200Day(),
                r.sharesOutstanding(),
                r.fiscalYearEnd()
        );
    }

    static RawBalanceSheet toRawBalanceSheet(BalanceSheetResponse r) {
        return new RawBalanceSheet(
                r.symbol(),
                mapBalanceSheetReports(r.annualReports()),
                mapBalanceSheetReports(r.quarterlyReports())
        );
    }

    static RawIncomeStatement toRawIncomeStatement(IncomeStatementResponse r) {
        return new RawIncomeStatement(
                r.symbol(),
                mapIncomeStatementReports(r.annualReports()),
                mapIncomeStatementReports(r.quarterlyReports())
        );
    }

    private static List<RawBalanceSheet.Report> mapBalanceSheetReports(List<BalanceSheetReport> reports) {
        if (reports == null) return List.of();
        return reports.stream()
                .map(AlphaVantageResponseMapper::toRawBalanceSheetReport)
                .toList();
    }

    private static RawBalanceSheet.Report toRawBalanceSheetReport(BalanceSheetReport r) {
        return new RawBalanceSheet.Report(
                r.fiscalDateEnding(),
                r.reportedCurrency(),
                r.totalAssets(),
                r.totalCurrentAssets(),
                r.totalNonCurrentAssets(),
                r.totalLiabilities(),
                r.totalCurrentLiabilities(),
                r.totalNonCurrentLiabilities(),
                r.totalShareholderEquity(),
                r.retainedEarnings(),
                r.commonStock(),
                r.cashAndCashEquivalents(),
                r.cashAndShortTermInvestments(),
                r.inventory(),
                r.currentNetReceivables(),
                r.shortTermDebt(),
                r.longTermDebt(),
                r.currentLongTermDebt(),
                r.longTermDebtNoncurrent(),
                r.shortLongTermDebtTotal(),
                r.commonStockSharesOutstanding()
        );
    }

    private static List<RawIncomeStatement.Report> mapIncomeStatementReports(List<IncomeStatementReport> reports) {
        if (reports == null) return List.of();
        return reports.stream()
                .map(AlphaVantageResponseMapper::toRawIncomeStatementReport)
                .toList();
    }

    private static RawIncomeStatement.Report toRawIncomeStatementReport(IncomeStatementReport r) {
        return new RawIncomeStatement.Report(
                r.fiscalDateEnding(),
                r.reportedCurrency(),
                r.grossProfit(),
                r.totalRevenue(),
                r.costOfRevenue(),
                r.costOfGoodsAndServicesSold(),
                r.operatingIncome(),
                r.sellingGeneralAndAdministrative(),
                r.researchAndDevelopment(),
                r.operatingExpenses(),
                r.netIncome(),
                r.ebit(),
                r.ebitda(),
                r.depreciationAndAmortization(),
                r.interestIncome(),
                r.interestExpense(),
                r.incomeTaxExpense(),
                r.incomeBeforeTax(),
                r.netIncomeFromContinuingOperations()
        );
    }
}
