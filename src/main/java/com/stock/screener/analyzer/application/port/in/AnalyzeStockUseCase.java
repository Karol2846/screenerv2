package com.stock.screener.analyzer.application.port.in;

import com.stock.screener.domain.valueobject.AnalysisReport;

public interface AnalyzeStockUseCase {
    AnalysisReport analyzeStock(String ticker);
}
