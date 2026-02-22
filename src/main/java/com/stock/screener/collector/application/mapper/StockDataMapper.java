package com.stock.screener.collector.application.mapper;

import com.stock.screener.collector.application.port.out.alphavantage.RawBalanceSheet;
import com.stock.screener.collector.application.port.out.alphavantage.RawCashFlow;
import com.stock.screener.collector.application.port.out.alphavantage.RawIncomeStatement;
import com.stock.screener.collector.application.port.out.alphavantage.RawOverview;
import com.stock.screener.collector.application.port.out.yhfinance.response.YhFinanceResponse;
import com.stock.screener.domain.valueobject.MarketData;
import com.stock.screener.domain.valueobject.snapshot.FinancialDataSnapshot;
import com.stock.screener.domain.valueobject.snapshot.MarketDataSnapshot;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class StockDataMapper {

    public MarketDataSnapshot toMarketDataSnapshot(RawOverview overview, YhFinanceResponse yhResponse) {
        var builder = MarketDataSnapshot.builder();

        if (overview != null) {
            builder.marketCap(overview.marketCapitalization())
                    .revenueTTM(overview.revenueTTM())
                    .forwardPeRatio(overview.forwardPE())
                    .targetPrice(overview.analystTargetPrice());
        }

        if (yhResponse != null) {
            builder.currentPrice(yhResponse.currentPrice())
                    .forwardEpsGrowth(yhResponse.forwardEpsGrowth())
                    .forwardRevenueGrowth(yhResponse.forwardRevenueGrowth())
                    .analystRatings(yhResponse.analystRatings());
        }

        return builder.build();
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
        var latestBalance = getLatestReportOrNull(
                balanceSheet != null ? balanceSheet.quarterlyReports() : null);
        var latestIncome = getLatestReportOrNull(
                incomeStatement != null ? incomeStatement.quarterlyReports() : null);
        var latestCash = getLatestReportOrNull(
                cashFlow != null ? cashFlow.quarterlyReports() : null);

        var builder = FinancialDataSnapshot.builder()
                .retainedEarnings(resolveRetainedEarnings(latestBalance))
                .ebit(resolveEbit(latestIncome))
                .totalDebt(calculateTotalDebt(latestBalance));

        if (latestBalance != null) {
            builder.totalCurrentAssets(latestBalance.totalCurrentAssets())
                    .totalCurrentLiabilities(latestBalance.totalCurrentLiabilities())
                    .totalAssets(latestBalance.totalAssets())
                    .totalLiabilities(latestBalance.totalLiabilities())
                    .totalShareholderEquity(latestBalance.totalShareholderEquity())
                    .inventory(latestBalance.inventory());
        }

        if (latestIncome != null) {
            builder.interestExpense(latestIncome.interestExpense())
                    .totalRevenue(latestIncome.totalRevenue())
                    .revenueTTM(calculateRevenueTTM(incomeStatement))
                    .netIncome(latestIncome.netIncome());
        }

        if (latestCash != null) {
            builder.operatingCashFlow(latestCash.operatingCashflow());
        }

        return builder.build();
    }

    // --- Private helpers ---

    /**
     * Calculates total debt using shortLongTermDebtTotal (pre-calculated by AlphaVantage).
     * Falls back to shortTermDebt + longTermDebt when the pre-calculated field is null.
     * NOTE: CSV spec says shortLongTermDebtTotal + longTermDebt, but that would double-count.
     * shortLongTermDebtTotal already includes both short- and long-term debt.
     */
    private BigDecimal calculateTotalDebt(RawBalanceSheet.Report balance) {
        if (balance == null) {
            return null;
        }
        if (balance.shortLongTermDebtTotal() != null) {
            return balance.shortLongTermDebtTotal();
        }
        BigDecimal shortTerm = balance.shortTermDebt() != null ? balance.shortTermDebt() : BigDecimal.ZERO;
        BigDecimal longTerm = balance.longTermDebt() != null ? balance.longTermDebt() : BigDecimal.ZERO;
        return shortTerm.add(longTerm);
    }

    /**
     * Resolves retained earnings from the balance sheet.
     * Fallback: totalShareholderEquity - (commonStock + additionalPaidInCapital).
     * Per CSV spec row 22: "If None: totalShareholderEquity - (commonStock + additionalPaidInCapital)."
     */
    private BigDecimal resolveRetainedEarnings(RawBalanceSheet.Report balance) {
        if (balance == null) {
            return null;
        }
        if (balance.retainedEarnings() != null) {
            return balance.retainedEarnings();
        }
        if (balance.totalShareholderEquity() != null && balance.commonStock() != null) {
            BigDecimal apic = balance.additionalPaidInCapital() != null
                    ? balance.additionalPaidInCapital()
                    : BigDecimal.ZERO;
            return balance.totalShareholderEquity()
                    .subtract(balance.commonStock())
                    .subtract(apic);
        }
        return null;
    }

    /**
     * Resolves EBIT from the income statement.
     * Fallback: netIncome + interestExpense + incomeTaxExpense (when ebit is null).
     */
    private BigDecimal resolveEbit(RawIncomeStatement.Report income) {
        if (income == null) {
            return null;
        }
        if (income.ebit() != null) {
            return income.ebit();
        }
        if (income.netIncome() != null) {
            BigDecimal tax = income.incomeTaxExpense() != null ? income.incomeTaxExpense() : BigDecimal.ZERO;
            BigDecimal interest = income.interestExpense() != null ? income.interestExpense() : BigDecimal.ZERO;
            return income.netIncome().add(interest).add(tax);
        }
        return null;
    }

    /**
     * Calculates Revenue TTM by summing totalRevenue from the 4 most recent quarterly reports.
     */
    private BigDecimal calculateRevenueTTM(RawIncomeStatement income) {
        if (income == null || income.quarterlyReports() == null || income.quarterlyReports().isEmpty()) {
            return null;
        }
        
        return income.quarterlyReports().stream()
                .limit(4)
                .map(RawIncomeStatement.Report::totalRevenue)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Generic helper to get the first (latest) report from a list, or null if empty/null.
     */
    private <T> T getLatestReportOrNull(List<T> reports) {
        return (reports == null || reports.isEmpty()) ? null : reports.getFirst();
    }
}
