package com.stock.screener.collector.adapter.out.file;

import com.stock.screener.collector.application.port.out.file.TickerReaderException;
import com.stock.screener.collector.application.port.out.file.TickerReaderPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@ApplicationScoped
public class FileTickerReaderAdapter implements TickerReaderPort {

    @ConfigProperty(name = "screener.tickers.file.path", defaultValue = "tickers.txt")
    String tickersFilePath;

    @Override
    public List<String> readTickers() {
        log.info("Reading tickers from file: {}", tickersFilePath);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(tickersFilePath)) {
            if (is == null) {
                String errMsg = "File " + tickersFilePath + " not found in resources.";
                log.error(errMsg);
                throw new TickerReaderException(errMsg);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                List<String> tickers = reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .toList();

                log.info("Successfully loaded {} tickers.", tickers.size());
                return tickers;
            }
        } catch (TickerReaderException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error reading ticker file: {}", tickersFilePath, e);
            throw new TickerReaderException("Failed to read tickers from: " + tickersFilePath, e);
        }
    }
}
