package com.stock.screener.collector.application.port.out.file;

import java.util.List;

/**
 * Port wyjściowy odpowiedzialny za dostarczanie listy symboli giełdowych,
 * które mają zostać poddane procesowi zbierania danych.
 */
public interface TickerReaderPort {

    /**
     * Odczytuje symbole spółek (np. "AAPL", "MSFT") ze zdefiniowanego źródła (np.
     * pliku tekstowego).
     *
     * @return Lista symboli giełdowych do analizy
     */
    List<String> readTickers();
}
