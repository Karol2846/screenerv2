package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

@Embeddable
public record AnalystRatings(
        Integer strongBuy,
        Integer buy,
        Integer hold,
        Integer sell,
        Integer strongSell
) {}
