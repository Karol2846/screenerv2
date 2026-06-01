package com.stock.screener.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sector enum — fromString mapping (Morningstar taxonomy)")
class SectorTest {

    @Nested
    @DisplayName("AlphaVantage (RapidAPI) — ALL CAPS strings")
    class AlphaVantageAllCaps {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "TECHNOLOGY,          TECHNOLOGY",
                "HEALTHCARE,          HEALTHCARE",
                "FINANCIAL SERVICES,  FINANCE",
                "ENERGY,              ENERGY",
                "CONSUMER DISCRETIONARY, CONSUMER_DISCRETIONARY",
                "CONSUMER CYCLICAL,   CONSUMER_DISCRETIONARY",
                "CONSUMER DEFENSIVE,  CONSUMER_DEFENSIVE",
                "REAL ESTATE,         REAL_ESTATE",
                "UTILITIES,           UTILITIES",
                "BASIC MATERIALS,     MINING",
                "INDUSTRIALS,         INDUSTRIALS",
                "COMMUNICATION SERVICES, COMMUNICATION_SERVICES"
        })
        @DisplayName("AlphaVantage ALL CAPS sector strings map to correct enum constant")
        void testAlphaVantageAllCapsMappings(String apiString, String expectedConstant) {
            // Given: a sector string returned by AlphaVantage (RapidAPI) in ALL CAPS
            // When: resolving the enum constant
            Sector result = Sector.fromString(apiString.strip());

            // Then: it maps to the expected enum constant
            assertThat(result).isEqualTo(Sector.valueOf(expectedConstant));
        }
    }

    @Nested
    @DisplayName("Yahoo Finance (assetProfile) — Title Case strings")
    class YahooFinanceTitleCase {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "Technology,              TECHNOLOGY",
                "Healthcare,              HEALTHCARE",
                "Financial Services,      FINANCE",
                "Energy,                  ENERGY",
                "Consumer Discretionary,  CONSUMER_DISCRETIONARY",
                "Consumer Cyclical,       CONSUMER_DISCRETIONARY",
                "Consumer Defensive,      CONSUMER_DEFENSIVE",
                "Real Estate,             REAL_ESTATE",
                "Utilities,               UTILITIES",
                "Basic Materials,         MINING",
                "Industrials,             INDUSTRIALS",
                "Communication Services,  COMMUNICATION_SERVICES"
        })
        @DisplayName("Yahoo Finance Title Case sector strings map to correct enum constant")
        void testYahooFinanceTitleCaseMappings(String apiString, String expectedConstant) {
            // Given: a sector string returned by Yahoo Finance (assetProfile) in Title Case
            // When: resolving the enum constant
            Sector result = Sector.fromString(apiString.strip());

            // Then: it maps to the expected enum constant
            assertThat(result).isEqualTo(Sector.valueOf(expectedConstant));
        }
    }

    @Nested
    @DisplayName("Legacy / edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Legacy 'Finance' (old displayName) still maps to FINANCE")
        void testLegacyFinanceString() {
            // Given: old displayName value stored in DB or config
            // When
            Sector result = Sector.fromString("Finance");
            // Then
            assertThat(result).isEqualTo(Sector.FINANCE);
        }

        @Test
        @DisplayName("Legacy 'Mining' still maps to MINING")
        void testLegacyMiningString() {
            // Given / When
            Sector result = Sector.fromString("Mining");
            // Then
            assertThat(result).isEqualTo(Sector.MINING);
        }

        @Test
        @DisplayName("Unknown / null-ish strings fall back to OTHER")
        void testUnknownStringFallsBackToOther() {
            // Given / When / Then
            assertThat(Sector.fromString("UNKNOWN SECTOR")).isEqualTo(Sector.OTHER);
            assertThat(Sector.fromString("")).isEqualTo(Sector.OTHER);
            assertThat(Sector.fromString("   ")).isEqualTo(Sector.OTHER);
        }

        @Test
        @DisplayName("Matching is case-insensitive (mixed case)")
        void testCaseInsensitivity() {
            // Given / When / Then
            assertThat(Sector.fromString("technology")).isEqualTo(Sector.TECHNOLOGY);
            assertThat(Sector.fromString("financial services")).isEqualTo(Sector.FINANCE);
            assertThat(Sector.fromString("Communication Services")).isEqualTo(Sector.COMMUNICATION_SERVICES);
        }
    }
}
