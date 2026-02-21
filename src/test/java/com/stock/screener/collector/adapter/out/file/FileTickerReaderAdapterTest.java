package com.stock.screener.collector.adapter.out.file;

import com.stock.screener.collector.application.port.out.file.TickerReaderException;
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
    @DisplayName("Successfully reads list of tickers from file ignoring comments and empty lines")
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
    @DisplayName("Throws TickerReaderException when file does not exist")
    void testThrowsExceptionWhenFileNotFound() {
        // Given
        adapter.tickersFilePath = "non-existing-file.txt";

        // When & Then
        assertThatThrownBy(() -> adapter.readTickers())
                .isInstanceOf(TickerReaderException.class)
                .hasMessageContaining("not found in resources");
    }
}
