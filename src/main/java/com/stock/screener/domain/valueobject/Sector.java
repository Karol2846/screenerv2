package com.stock.screener.domain.valueobject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sector {
    TECHNOLOGY("Technology"),
    HEALTHCARE("Healthcare"),
    FINANCE("Financial Services"),
    ENERGY("Energy"),
    CONSUMER_DISCRETIONARY("Consumer Cyclical"),
    CONSUMER_STAPLES("Consumer Defensive"),
    INDUSTRIALS("Industrials"),
    COMMUNICATION_SERVICES("Communication Services"),
    BASIC_MATERIALS("Basic Materials"),
    REAL_ESTATE("Real Estate"),
    UTILITIES("Utilities"),
    OTHER("Other");

    private final String displayName;

    public static Sector fromString(String value) {
        for (var sector: Sector.values()) {
            if (sector.displayName.equalsIgnoreCase(value)) {
                return sector;
            }
        }
        return OTHER;
    }
}
