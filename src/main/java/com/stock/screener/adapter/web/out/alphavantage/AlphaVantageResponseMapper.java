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
        return RawOverview.builder()
                .symbol(r.symbol())
                .assetType(r.assetType())
                .name(r.name())
                .exchange(r.exchange())
                .currency(r.currency())
                .country(r.country())
                .sector(r.sector())
                .industry(r.industry())
                .marketCapitalization(r.marketCapitalization())
                .ebitda(r.ebitda())
                .peRatio(r.peRatio())
                .pegRatio(r.pegRatio())
                .bookValue(r.bookValue())
                .dividendPerShare(r.dividendPerShare())
                .dividendYield(r.dividendYield())
                .eps(r.eps())
                .revenuePerShareTTM(r.revenuePerShareTTM())
                .profitMargin(r.profitMargin())
                .operatingMarginTTM(r.operatingMarginTTM())
                .returnOnAssetsTTM(r.returnOnAssetsTTM())
                .returnOnEquityTTM(r.returnOnEquityTTM())
                .revenueTTM(r.revenueTTM())
                .grossProfitTTM(r.grossProfitTTM())
                .quarterlyEarningsGrowthYOY(r.quarterlyEarningsGrowthYOY())
                .quarterlyRevenueGrowthYOY(r.quarterlyRevenueGrowthYOY())
                .analystTargetPrice(r.analystTargetPrice())
                .analystRatingStrongBuy(r.analystRatingStrongBuy())
                .analystRatingBuy(r.analystRatingBuy())
                .analystRatingHold(r.analystRatingHold())
                .analystRatingSell(r.analystRatingSell())
                .analystRatingStrongSell(r.analystRatingStrongSell())
                .trailingPE(r.trailingPE())
                .forwardPE(r.forwardPE())
                .priceToSalesRatioTTM(r.priceToSalesRatioTTM())
                .priceToBookRatio(r.priceToBookRatio())
                .evToRevenue(r.evToRevenue())
                .evToEBITDA(r.evToEBITDA())
                .beta(r.beta())
                .weekHigh52(r.weekHigh52())
                .weekLow52(r.weekLow52())
                .movingAverage50Day(r.movingAverage50Day())
                .movingAverage200Day(r.movingAverage200Day())
                .sharesOutstanding(r.sharesOutstanding())
                .fiscalYearEnd(r.fiscalYearEnd())
                .build();
    }

    static RawBalanceSheet toRawBalanceSheet(BalanceSheetResponse r) {
        return RawBalanceSheet.builder()
                .symbol(r.symbol())
                .annualReports(mapBalanceSheetReports(r.annualReports()))
                .quarterlyReports(mapBalanceSheetReports(r.quarterlyReports()))
                .build();
    }

    static RawIncomeStatement toRawIncomeStatement(IncomeStatementResponse r) {
        return RawIncomeStatement.builder()
                .symbol(r.symbol())
                .annualReports(mapIncomeStatementReports(r.annualReports()))
                .quarterlyReports(mapIncomeStatementReports(r.quarterlyReports()))
                .build();
    }

    private static List<RawBalanceSheet.Report> mapBalanceSheetReports(List<BalanceSheetReport> reports) {
        if (reports == null) return List.of();
        return reports.stream()
                .map(AlphaVantageResponseMapper::toRawBalanceSheetReport)
                .toList();
    }

    private static RawBalanceSheet.Report toRawBalanceSheetReport(BalanceSheetReport r) {
        return RawBalanceSheet.Report.builder()
                .fiscalDateEnding(r.fiscalDateEnding())
                .reportedCurrency(r.reportedCurrency())
                .totalAssets(r.totalAssets())
                .totalCurrentAssets(r.totalCurrentAssets())
                .totalNonCurrentAssets(r.totalNonCurrentAssets())
                .totalLiabilities(r.totalLiabilities())
                .totalCurrentLiabilities(r.totalCurrentLiabilities())
                .totalNonCurrentLiabilities(r.totalNonCurrentLiabilities())
                .totalShareholderEquity(r.totalShareholderEquity())
                .retainedEarnings(r.retainedEarnings())
                .commonStock(r.commonStock())
                .cashAndCashEquivalents(r.cashAndCashEquivalents())
                .cashAndShortTermInvestments(r.cashAndShortTermInvestments())
                .inventory(r.inventory())
                .currentNetReceivables(r.currentNetReceivables())
                .shortTermDebt(r.shortTermDebt())
                .longTermDebt(r.longTermDebt())
                .currentLongTermDebt(r.currentLongTermDebt())
                .longTermDebtNoncurrent(r.longTermDebtNoncurrent())
                .shortLongTermDebtTotal(r.shortLongTermDebtTotal())
                .commonStockSharesOutstanding(r.commonStockSharesOutstanding())
                .build();
    }

    private static List<RawIncomeStatement.Report> mapIncomeStatementReports(List<IncomeStatementReport> reports) {
        if (reports == null) return List.of();
        return reports.stream()
                .map(AlphaVantageResponseMapper::toRawIncomeStatementReport)
                .toList();
    }

    private static RawIncomeStatement.Report toRawIncomeStatementReport(IncomeStatementReport r) {
        return RawIncomeStatement.Report.builder()
                .fiscalDateEnding(r.fiscalDateEnding())
                .reportedCurrency(r.reportedCurrency())
                .grossProfit(r.grossProfit())
                .totalRevenue(r.totalRevenue())
                .costOfRevenue(r.costOfRevenue())
                .costOfGoodsAndServicesSold(r.costOfGoodsAndServicesSold())
                .operatingIncome(r.operatingIncome())
                .sellingGeneralAndAdministrative(r.sellingGeneralAndAdministrative())
                .researchAndDevelopment(r.researchAndDevelopment())
                .operatingExpenses(r.operatingExpenses())
                .netIncome(r.netIncome())
                .ebit(r.ebit())
                .ebitda(r.ebitda())
                .depreciationAndAmortization(r.depreciationAndAmortization())
                .interestIncome(r.interestIncome())
                .interestExpense(r.interestExpense())
                .incomeTaxExpense(r.incomeTaxExpense())
                .incomeBeforeTax(r.incomeBeforeTax())
                .netIncomeFromContinuingOperations(r.netIncomeFromContinuingOperations())
                .build();
    }
}
