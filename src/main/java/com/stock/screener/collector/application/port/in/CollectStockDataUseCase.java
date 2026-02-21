package com.stock.screener.collector.application.port.in;

import com.stock.screener.domain.entity.Stock;

public interface CollectStockDataUseCase {
    Stock collectDataForStock(String ticker);
}
