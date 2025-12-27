package com.stock.screener.adapter.web.out.model.screener;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Metadane kryteriów screenera.
 */
@Builder
@Jacksonized
public record ScreenerCriteriaMeta(
        Integer size,
        Integer offset,
        String sortField,
        String sortType,
        String quoteType,
        String topOperator,
        List<ScreenerCriterion> criteria
) {

    @Builder
    @Jacksonized
    public record ScreenerCriterion(
            String field,
            List<String> operators,
            List<Object> values,
            List<Integer> labelsSelected
    ) {
    }
}

