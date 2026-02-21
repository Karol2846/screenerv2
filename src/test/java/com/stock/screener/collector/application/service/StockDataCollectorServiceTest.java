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

    @InjectMocks
    private StockDataCollectorService service;

    @BeforeEach
    void setUp() {
        // W tej chwili collectDataForStock ma tylko szkielet (zwraca null i loguje).
        // Wykorzystamy powiązanie z Mocks by sprawdzić przepływ.
    }

    @Test
    @DisplayName("Odczytuje wszystkie tickery i wywołuje na nich process, ignorując te, które zgłaszają błąd")
    void testCollectDataForAllTickers_ShouldAttemptAllDespiteErrors() {
        // Given
        when(tickerReaderPort.readTickers()).thenReturn(List.of("AAPL", "ERROR", "MSFT"));

        // Zastępujemy serwis spytargetem żeby wyrzucić wyjątek dla "ERROR" i
        // nasłuchiwać collectDataForStock
        StockDataCollectorService spyService = spy(new StockDataCollectorService(
                alphaVantageClient,
                yahooFinanceClient,
                tickerReaderPort));

        // Stubujemy zachowanie konkretnej metody chronionej w tej samej klasie
        doReturn(null).when(spyService).collectDataForStock("AAPL");
        doThrow(new RuntimeException("API Limit exeeded")).when(spyService).collectDataForStock("ERROR");
        doReturn(null).when(spyService).collectDataForStock("MSFT");

        // When
        spyService.collectDataForAllTickers();

        // Then
        // Upewniamy się, że pomimo wywalenia dla "ERROR", program zaczął pobieranie dla
        // "MSFT"
        verify(spyService, times(1)).collectDataForStock("AAPL");
        verify(spyService, times(1)).collectDataForStock("ERROR");
        verify(spyService, times(1)).collectDataForStock("MSFT");
    }
}
