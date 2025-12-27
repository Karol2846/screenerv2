package com.stock.screener.adapter.web.out.model.insights;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wynik analizy insights dla symbolu.
 */
@Builder
@Jacksonized
public record InsightsResult(
        String symbol,
        CompanySnapshot companySnapshot,
        InstrumentInfo instrumentInfo,
        List<Report> reports
) {

    @Builder
    @Jacksonized
    public record CompanySnapshot(
            CompanyMetrics company,
            CompanyMetrics sector,
            String sectorInfo
    ) {
    }

    @Builder
    @Jacksonized
    public record CompanyMetrics(
            Double dividends,
            Double earningsReports,
            Double hiring,
            Double innovativeness,
            Double insiderSentiments,
            Double sustainability
    ) {
    }

    @Builder
    @Jacksonized
    public record InstrumentInfo(
            KeyTechnicals keyTechnicals,
            Recommendation recommendation,
            TechnicalEvents technicalEvents,
            Valuation valuation
    ) {
    }

    @Builder
    @Jacksonized
    public record KeyTechnicals(
            String provider,
            Double resistance,
            Double stopLoss,
            Double support
    ) {
    }

    @Builder
    @Jacksonized
    public record Recommendation(
            String provider,
            String rating,
            Double targetPrice
    ) {
    }

    @Builder
    @Jacksonized
    public record TechnicalEvents(
            String provider,
            String shortTerm,
            String midTerm,
            String longTerm
    ) {
    }

    @Builder
    @Jacksonized
    public record Valuation(
            String provider,
            Integer color,
            String description,
            String discount,
            String relativeValue
    ) {
    }

    @Builder
    @Jacksonized
    public record Report(
            String id,
            String provider,
            String publishedOn,
            String summary,
            String title
    ) {
    }
}

