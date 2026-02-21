package com.stock.screener.collector.adapter.out.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("File Ticker Reader Adapter Tests")
class FileTickerReaderAdapterTest {

    private FileTickerReaderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FileTickerReaderAdapter();
    }

    @Test
    @DisplayName("Pomyślnie odczytuje listę tickerów z pliku ignorując komentarze, puste linie i spacje")
    void testReadTickersSuccessfully() {
        // Given
        adapter.tickersFilePath = "test-tickers.txt";

        // When
        List<String> result = adapter.readTickers();

        // Then
        assertThat(result)
                .hasSize(4)
                .containsExactly("AAPL", "MSFT", "GOOGL", "NVDA");
    }

    @Test
    @DisplayName("Rzuca IllegalStateException, gdy plik nie istnieje")
    void testThrowsExceptionWhenFileNotFound() {
        // Given
        adapter.tickersFilePath = "nie-istnejacy-plik.txt";

        // When & Then
        assertThatThrownBy(() -> adapter.readTickers())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nie został znaleziony w resources. Wymagany plik");
    }
}
