package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record IncomeStatementHistoryQuarterly(
        List<IncomeStatement> incomeStatementHistory,
        Integer maxAge
) {
}

