package com.stock.screener.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sector Enum Tests")
class SectorTest {

    @ParameterizedTest(name = "Alpha Vantage sector \"{0}\" maps to {1}")
    @CsvSource({
            "Technology,         TECHNOLOGY",
            "Healthcare,         HEALTHCARE",
            "Financial Services, FINANCE",
            "Energy,             ENERGY",
            "Consumer Cyclical,  CONSUMER_DISCRETIONARY",
            "Consumer Defensive, CONSUMER_STAPLES",
            "Industrials,        INDUSTRIALS",
            "Communication Services, COMMUNICATION_SERVICES",
            "Basic Materials,    BASIC_MATERIALS",
            "Real Estate,        REAL_ESTATE",
            "Utilities,          UTILITIES"
    })
    @DisplayName("Standard GICS sectors from Alpha Vantage are correctly mapped")
    void standardSectors_shouldMapCorrectly(String apiValue, Sector expected) {
        assertThat(Sector.fromString(apiValue)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Unknown sector string maps to OTHER")
    void unknownSector_shouldMapToOther() {
        assertThat(Sector.fromString("Nonexistent Sector")).isEqualTo(Sector.OTHER);
    }

    @Test
    @DisplayName("Null sector string maps to OTHER")
    void nullSector_shouldMapToOther() {
        assertThat(Sector.fromString(null)).isEqualTo(Sector.OTHER);
    }

    @Test
    @DisplayName("Case-insensitive matching works")
    void caseInsensitiveMatching_shouldWork() {
        assertThat(Sector.fromString("technology")).isEqualTo(Sector.TECHNOLOGY);
        assertThat(Sector.fromString("TECHNOLOGY")).isEqualTo(Sector.TECHNOLOGY);
        assertThat(Sector.fromString("basic materials")).isEqualTo(Sector.BASIC_MATERIALS);
    }
}
