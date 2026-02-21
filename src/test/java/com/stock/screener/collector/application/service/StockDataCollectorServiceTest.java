package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stock Data Collector Service Tests")
class StockDataCollectorServiceTest {

    @Mock
    private AlphaVantageClient alphaVantageClient;

    @Mock
    private YahooFinanceClient yahooFinanceClient;

    @Mock
    private TickerReaderPort tickerReaderPort;

    @Mock
    private com.stock.screener.collector.application.mapper.StockDataMapper stockDataMapper;

    @InjectMocks
    private StockDataCollectorService service;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Reads all tickers and calls process, skipping those that throw errors")
    void testCollectDataForAllTickers_ShouldAttemptAllDespiteErrors() {
        // Given
        when(tickerReaderPort.readTickers()).thenReturn(List.of("AAPL", "ERROR", "MSFT"));

        StockDataCollectorService spyService = spy(new StockDataCollectorService(
                alphaVantageClient,
                yahooFinanceClient,
                tickerReaderPort,
                stockDataMapper));

        doReturn(null).when(spyService).collectDataForStock("AAPL");
        doThrow(new RuntimeException("API Limit exceeded")).when(spyService).collectDataForStock("ERROR");
        doReturn(null).when(spyService).collectDataForStock("MSFT");

        // When
        spyService.collectDataForAllTickers();

        // Then
        verify(spyService, times(1)).collectDataForStock("AAPL");
        verify(spyService, times(1)).collectDataForStock("ERROR");
        verify(spyService, times(1)).collectDataForStock("MSFT");
    }
}
