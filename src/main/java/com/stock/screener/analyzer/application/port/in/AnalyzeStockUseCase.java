package com.stock.screener.analyzer.application.port.in;

import com.stock.screener.analyzer.application.service.AnalysisReport;

public interface AnalyzeStockUseCase {
    AnalysisReport analyzeStock(String ticker);
}
