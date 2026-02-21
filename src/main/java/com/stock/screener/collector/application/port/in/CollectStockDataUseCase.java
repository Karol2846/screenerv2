package com.stock.screener.collector.application.port.in;

import com.stock.screener.domain.entity.Stock;

/**
 * Port wejściowy do zbierania (pobierania/odświeżania) i zapisywania danych
 * spółki z zewnętrznych źródeł (Alpha Vantage, Yahoo Finance).
 */
public interface CollectStockDataUseCase {

    /**
     * Odświeża lub pobiera dane dla danego tickera.
     * Decyzja o tym, czy pobrać z API czy użyć bazy (w oparciu o czas ostatniej
     * aktualizacji),
     * jest logiką ukrytą za tą operacją.
     *
     * @param ticker Symbol spółki (np. "AAPL")
     * @return Zaktualizowana encja Stock i powiązane raporty
     */
    Stock collectDataForStock(String ticker);
}
