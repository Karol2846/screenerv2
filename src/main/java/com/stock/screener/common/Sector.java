package com.stock.screener.common;

/**
 * Morningstar/Yahoo sector taxonomy — used by both AlphaVantage (RapidAPI, ALL CAPS)
 * and Yahoo Finance (assetProfile, Title Case). {@code fromString} matches any alias
 * case-insensitively and falls back to {@link #OTHER} for unknown values.
 *
 * <p>Verified against live APIs on 2026-06-01. See {@code docs/api-samples/} for raw responses.
 *
 * <p>Alias sets per constant:
 * <ul>
 *   <li>TECHNOLOGY          ← "Technology"
 *   <li>HEALTHCARE          ← "Healthcare"
 *   <li>FINANCE             ← "Finance", "Financial Services"
 *   <li>ENERGY              ← "Energy"
 *   <li>CONSUMER_DISCRETIONARY ← "Consumer Discretionary", "Consumer Cyclical"
 *   <li>CONSUMER_DEFENSIVE  ← "Consumer Defensive"
 *   <li>REAL_ESTATE         ← "Real Estate"
 *   <li>UTILITIES           ← "Utilities"
 *   <li>MINING              ← "Mining", "Basic Materials"
 *   <li>INDUSTRIALS         ← "Industrials"
 *   <li>COMMUNICATION_SERVICES ← "Communication Services"
 *   <li>OTHER               ← fallback for anything unrecognised
 * </ul>
 */
public enum Sector {

    TECHNOLOGY("Technology"),
    HEALTHCARE("Healthcare"),
    FINANCE("Finance", "Financial Services"),
    ENERGY("Energy"),
    CONSUMER_DISCRETIONARY("Consumer Discretionary", "Consumer Cyclical"),
    CONSUMER_DEFENSIVE("Consumer Defensive"),
    REAL_ESTATE("Real Estate"),
    UTILITIES("Utilities"),
    MINING("Mining", "Basic Materials"),
    INDUSTRIALS("Industrials"),
    COMMUNICATION_SERVICES("Communication Services"),
    OTHER("Other");

    private final String[] aliases;

    Sector(String... aliases) {
        this.aliases = aliases;
    }

    /**
     * Resolves a sector string from an external API to the matching enum constant.
     * Matching is case-insensitive across all registered aliases.
     *
     * @param value raw sector string from AlphaVantage or Yahoo Finance
     * @return the matching constant, or {@link #OTHER} if none matches
     */
    public static Sector fromString(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        String trimmed = value.strip();
        for (Sector sector : values()) {
            for (String alias : sector.aliases) {
                if (alias.equalsIgnoreCase(trimmed)) {
                    return sector;
                }
            }
        }
        return OTHER;
    }
}
