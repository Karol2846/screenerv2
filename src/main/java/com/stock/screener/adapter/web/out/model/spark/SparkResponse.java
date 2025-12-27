package com.stock.screener.adapter.web.out.model.spark;

import java.util.HashMap;

/**
 * Odpowiedz z endpointa /v8/finance/spark.
 *
 * API zwraca mape gdzie kluczem jest symbol, a wartoscia SparkData.
 * Przyklad: {"AAPL": {...}, "MSFT": {...}}
 */
public class SparkResponse extends HashMap<String, SparkData> {
}

