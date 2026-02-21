package com.stock.screener.collector.adapter.out.file;

import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class FileTickerReaderAdapter implements TickerReaderPort {

    @ConfigProperty(name = "screener.tickers.file.path", defaultValue = "tickers.txt")
    String tickersFilePath;

    @Override
    public List<String> readTickers() {
        log.info("Rozpoczęto wczytywanie tickerów z pliku: {}", tickersFilePath);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(tickersFilePath)) {
            if (is == null) {
                log.warn("Plik {} nie został znaleziony w resources. Zwracam pustą listę.", tickersFilePath);
                return Collections.emptyList();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                List<String> tickers = reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#")) // Ignoruj puste linie i komentarze
                        .collect(Collectors.toList());

                log.info("Pomyślnie wczytano {} tickerów.", tickers.size());
                return tickers;
            }
        } catch (Exception e) {
            log.error("Błąd podczas wczytywania pliku z tickerami: {}", tickersFilePath, e);
            return Collections.emptyList();
        }
    }
}
