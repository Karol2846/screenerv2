package com.stock.screener.collector.application.mapper;

import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.collector.application.port.out.alphavantage.RawCashFlow;
import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;
import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.domain.valueobject.MarketData;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApplicationScoped
public class StockDataMapper {

    public MarketDataSnapshot toMarketDataSnapshot(RawOverview overview, YhFinanceResponse yhResponse) {
        BigDecimal forwardEpsGrowth = yhResponse != null ? yhResponse.forwardEpsGrowth() : null;
        BigDecimal forwardRevenueGrowth = yhResponse != null ? yhResponse.forwardRevenueGrowth() : null;
        var analystRatings = yhResponse != null ? yhResponse.analystRatings() : null;

        return MarketDataSnapshot.builder()
                .currentPrice(overview != null ? overview.movingAverage50Day() : null) // Używamy ruchomej średniej jako
                                                                                       // atrapy ceny (Alpha Vantage
                                                                                       // docelowo wspiera to przez
                                                                                       // osobny endpoint Global Quote,
                                                                                       // zbadamy to na etapie
                                                                                       // poprawiania dokładności wycen)
                .marketCap(overview != null ? overview.marketCapitalization() : null)
                .revenueTTM(overview != null ? overview.revenueTTM() : null)
                .forwardPeRatio(overview != null ? overview.forwardPE() : null)
                .targetPrice(overview != null ? overview.analystTargetPrice() : null)
                .forwardEpsGrowth(forwardEpsGrowth)
                .forwardRevenueGrowth(forwardRevenueGrowth)
                .analystRatings(analystRatings)
                .build();
    }

    public MarketData toMarketData(MarketDataSnapshot snapshot) {
        return MarketData.builder()
                .currentPrice(snapshot.currentPrice())
                .marketCap(snapshot.marketCap())
                .forwardPeRatio(snapshot.forwardPeRatio())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    public FinancialDataSnapshot toFinancialDataSnapshot(RawBalanceSheet balanceSheet,
            RawIncomeStatement incomeStatement, RawCashFlow cashFlow) {
        var latestBalance = getLatestReportOrNull(balanceSheet);
        var latestIncome = getLatestReportOrNull(incomeStatement);
        var latestCash = getLatestReportOrNull(cashFlow);

        BigDecimal shortLongTermDebtTotal = latestBalance != null && latestBalance.shortLongTermDebtTotal() != null
                ? latestBalance.shortLongTermDebtTotal()
                : BigDecimal.ZERO;
        BigDecimal longTermDebt = latestBalance != null && latestBalance.longTermDebt() != null
                ? latestBalance.longTermDebt()
                : BigDecimal.ZERO;

        BigDecimal totalDebt = shortLongTermDebtTotal.add(longTermDebt);

        BigDecimal totalCurrentAssets = latestBalance != null ? latestBalance.totalCurrentAssets() : null;
        BigDecimal totalCurrentLiabilities = latestBalance != null ? latestBalance.totalCurrentLiabilities() : null;
        BigDecimal totalAssets = latestBalance != null ? latestBalance.totalAssets() : null;
        BigDecimal totalLiabilities = latestBalance != null ? latestBalance.totalLiabilities() : null;
        BigDecimal totalShareholderEquity = latestBalance != null ? latestBalance.totalShareholderEquity() : null;

        // Inventory: Null-safe -> 0
        BigDecimal inventory = latestBalance != null && latestBalance.inventory() != null
                ? latestBalance.inventory()
                : BigDecimal.ZERO;

        // Retained Earnings: Fallback -> totalShareholderEquity - commonStock
        BigDecimal retainedEarnings = latestBalance != null ? latestBalance.retainedEarnings() : null;
        if (retainedEarnings == null && totalShareholderEquity != null && latestBalance.commonStock() != null) {
            retainedEarnings = totalShareholderEquity.subtract(latestBalance.commonStock());
        }

        BigDecimal netIncome = latestIncome != null ? latestIncome.netIncome() : null;
        BigDecimal interestExpense = latestIncome != null ? latestIncome.interestExpense() : null;

        // EBIT: Fallback -> netIncome + interestExpense + incomeTaxExpense
        BigDecimal ebit = latestIncome != null ? latestIncome.ebit() : null;
        if (ebit == null && netIncome != null) {
            BigDecimal tax = latestIncome.incomeTaxExpense() != null ? latestIncome.incomeTaxExpense()
                    : BigDecimal.ZERO;
            BigDecimal intExp = interestExpense != null ? interestExpense : BigDecimal.ZERO;
            ebit = netIncome.add(intExp).add(tax);
        }

        return FinancialDataSnapshot.builder()
                .totalCurrentAssets(totalCurrentAssets)
                .totalCurrentLiabilities(totalCurrentLiabilities)
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .retainedEarnings(retainedEarnings)
                .ebit(ebit)
                .interestExpense(interestExpense)
                .totalShareholderEquity(totalShareholderEquity)
                .inventory(inventory)
                .totalRevenue(latestIncome != null ? latestIncome.totalRevenue() : null)
                .totalDebt(totalDebt)
                .netIncome(netIncome)
                .operatingCashFlow(latestCash != null ? latestCash.operatingCashflow() : null)
                .build();
    }

    private RawBalanceSheet.Report getLatestReportOrNull(RawBalanceSheet sheet) {
        if (sheet == null || sheet.quarterlyReports() == null || sheet.quarterlyReports().isEmpty())
            return null;
        return sheet.quarterlyReports().getFirst();
    }

    private RawIncomeStatement.Report getLatestReportOrNull(RawIncomeStatement idx) {
        if (idx == null || idx.quarterlyReports() == null || idx.quarterlyReports().isEmpty())
            return null;
        return idx.quarterlyReports().getFirst();
    }

    private RawCashFlow.Report getLatestReportOrNull(RawCashFlow flow) {
        if (flow == null || flow.quarterlyReports() == null || flow.quarterlyReports().isEmpty())
            return null;
        return flow.quarterlyReports().getFirst();
    }
}
