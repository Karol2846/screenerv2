package com.stock.screener.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sector Enum Tests")
class SectorTest {

    @Nested
    @DisplayName("Alpha Vantage API Value Mapping")
    class AlphaVantageMapping {

        @ParameterizedTest(name = "fromString(\"{0}\") should return {1}")
        @MethodSource("com.stock.screener.domain.valueobject.SectorTest#alphaVantageValueProvider")
        @DisplayName("All Alpha Vantage sector values map to correct enum constants")
        void testAlphaVantageSectorMapping(String apiValue, Sector expected) {
            assertThat(Sector.fromString(apiValue)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Case Insensitivity")
    class CaseInsensitivity {

        @Test
        @DisplayName("fromString should be case-insensitive")
        void testCaseInsensitive() {
            assertThat(Sector.fromString("technology")).isEqualTo(Sector.TECHNOLOGY);
            assertThat(Sector.fromString("TECHNOLOGY")).isEqualTo(Sector.TECHNOLOGY);
            assertThat(Sector.fromString("Technology")).isEqualTo(Sector.TECHNOLOGY);
        }
    }

    @Nested
    @DisplayName("Unknown Values")
    class UnknownValues {

        @Test
        @DisplayName("Unknown sector string should return OTHER")
        void testUnknownSectorReturnsOther() {
            assertThat(Sector.fromString("UnknownSector")).isEqualTo(Sector.OTHER);
        }

        @Test
        @DisplayName("Empty string should return OTHER")
        void testEmptyStringReturnsOther() {
            assertThat(Sector.fromString("")).isEqualTo(Sector.OTHER);
        }
    }

    static Stream<Arguments> alphaVantageValueProvider() {
        return Stream.of(
                Arguments.of("Technology", Sector.TECHNOLOGY),
                Arguments.of("Healthcare", Sector.HEALTHCARE),
                Arguments.of("Financial Services", Sector.FINANCE),
                Arguments.of("Energy", Sector.ENERGY),
                Arguments.of("Consumer Cyclical", Sector.CONSUMER_DISCRETIONARY),
                Arguments.of("Consumer Defensive", Sector.CONSUMER_STAPLES),
                Arguments.of("Industrials", Sector.INDUSTRIALS),
                Arguments.of("Communication Services", Sector.COMMUNICATION_SERVICES),
                Arguments.of("Basic Materials", Sector.BASIC_MATERIALS),
                Arguments.of("Real Estate", Sector.REAL_ESTATE),
                Arguments.of("Utilities", Sector.UTILITIES)
        );
    }
}
