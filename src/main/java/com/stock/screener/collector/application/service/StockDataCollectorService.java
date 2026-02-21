package com.stock.screener.collector.application.service;

import com.stock.screener.collector.application.port.in.CollectStockDataUseCase;
import com.stock.screener.collector.application.port.out.alphavantage.AlphaVantageClient;
import com.stock.screener.collector.application.port.out.yhfinance.YahooFinanceClient;
import com.stock.screener.domain.entity.Stock;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StockDataCollectorService implements CollectStockDataUseCase {

    private final AlphaVantageClient alphaVantageClient;
    private final YahooFinanceClient yahooFinanceClient;
    private final com.stock.screener.collector.application.port.out.file.TickerReaderPort tickerReaderPort;

    @Override
    public Stock collectDataForStock(String ticker) {
        log.info("Rozpoczęcie zbierania danych dla: {}", ticker);

        // TODO: 1. Sprawdź, czy dane są w bazie danych. (np. Stock.findById(ticker))

        // TODO: 2. Sprawdź wiek poszczególnych raportów (wg rules z work_plan.md np.
        // `lastUpdateTime < 90 dni`).

        // TODO: 3. Jeśli brakuje danych, wykonaj pytania do portów zewnętrznych
        // (alphaVantageClient oraz yahooFinanceClient) i mapuj je do domeny.

        // TODO: 4. Połącz dane (np. updateMetrics), zaktualizuj Stock i zapisz w bazie
        // (np. stock.persist()).

        // Tymczasowo zwracany null - zarys interfejsu (na razie tworzymy tylko
        // architekturę)
        return null;
    }

    /**
     * Główna funkcja wywoływana np. przez Scheduler,
     * iteruje po wczytanej liście symboli i uruchamia proces collect dla
     * pojedynczych.
     */
    public void collectDataForAllTickers() {
        log.info("Uruchamiam Collection Pipeline dla wszystkich spółek z pliku");
        var tickers = tickerReaderPort.readTickers();

        for (String ticker : tickers) {
            try {
                collectDataForStock(ticker);
            } catch (Exception ex) {
                log.error("Nie udało się pobrać i zaktualizować danych dla tickera: {}", ticker, ex);
                // Kontynuujemy z kolejnym...
            }
        }
        log.info("Collection Pipeline zakończony.");
    }
}
