package com.stock.screener.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sector {
    TECHNOLOGY("Technology"),
    HEALTHCARE("Healthcare"),
    FINANCE("Finance"),
    ENERGY("Energy"),
    CONSUMER_DISCRETIONARY("Consumer Discretionary"),
    REAL_ESTATE("Real Estate"),
    UTILITIES("Utilities"),
    MINING("Mining"),
    CONSUMER_CYCLICAL("CONSUMER CYCLICAL"),
    COMMUNICATION_SERVICES("COMMUNICATION SERVICES"),
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
