package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record IncomeStatement(
        Integer maxAge,
        RawFmtValue endDate,
        RawFmtValue totalRevenue,
        RawFmtValue costOfRevenue,
        RawFmtValue grossProfit,
        RawFmtValue researchDevelopment,
        RawFmtValue sellingGeneralAdministrative,
        RawFmtValue nonRecurring,
        RawFmtValue otherOperatingExpenses,
        RawFmtValue totalOperatingExpenses,
        RawFmtValue operatingIncome,
        RawFmtValue totalOtherIncomeExpenseNet,
        RawFmtValue ebit,
        RawFmtValue interestExpense,
        RawFmtValue incomeBeforeTax,
        RawFmtValue incomeTaxExpense,
        RawFmtValue minorityInterest,
        RawFmtValue netIncomeFromContinuingOps,
        RawFmtValue discontinuedOperations,
        RawFmtValue extraordinaryItems,
        RawFmtValue effectOfAccountingCharges,
        RawFmtValue otherItems,
        RawFmtValue netIncome,
        RawFmtValue netIncomeApplicableToCommonShares
) {
}

