package com.stock.screener.collector.application.port.out.file;

import java.util.List;

public interface TickerReaderPort {
    List<String> readTickers();
}
