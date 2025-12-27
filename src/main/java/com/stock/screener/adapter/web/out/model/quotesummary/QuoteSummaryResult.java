package com.stock.screener.adapter.web.out.model.quotesummary;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Wynik Quote Summary - może zawierać różne moduły danych.
 */
@Builder
@Jacksonized
public record QuoteSummaryResult(
        AssetProfile assetProfile,
        DefaultKeyStatistics defaultKeyStatistics,
        FinancialData financialData,
        SummaryDetail summaryDetail,
        CalendarEvents calendarEvents,
        Earnings earnings,
        Price price,
        QuoteType quoteType
) {
}

