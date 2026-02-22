package com.stock.screener.domain.valueobject.snapshot;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FinancialDataSnapshot(
                BigDecimal totalCurrentAssets,
                BigDecimal totalCurrentLiabilities,
                BigDecimal totalAssets,
                BigDecimal totalLiabilities,
                BigDecimal retainedEarnings,
                BigDecimal ebit,
                BigDecimal interestExpense,
                BigDecimal totalShareholderEquity,
                BigDecimal inventory,
                BigDecimal totalRevenue,
                BigDecimal totalDebt,
                BigDecimal netIncome,
                BigDecimal operatingCashFlow) {
}
