package com.stock.screener.adapter.web.out.model.screener;

import com.stock.screener.adapter.web.out.model.quote.Quote;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wynik screenera.
 */
@Builder
@Jacksonized
public record ScreenerResult(
        String id,
        String title,
        String description,
        String canonicalName,
        ScreenerCriteriaMeta criteriaMeta,
        String rawCriteria,
        Integer start,
        Integer count,
        Integer total,
        List<Quote> quotes,
        Boolean predefinedScr,
        Integer versionId,
        Long creationDate,
        Long lastUpdated
) {
}

