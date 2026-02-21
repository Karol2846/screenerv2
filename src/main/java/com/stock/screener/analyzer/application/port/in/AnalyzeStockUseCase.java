package com.stock.screener.analyzer.application.port.in;

import com.stock.screener.domain.valueobject.AnalysisReport;

/**
 * Port wejściowy inicjujący analizę i proces oceniania (Scoring) spółki
 * giełdowej.
 */
public interface AnalyzeStockUseCase {

    /**
     * Przeprowadza pełną analizę spółki według Pipeline'u zaplanowanego z
     * work_plan.md.
     * 
     * @param ticker Symbol spółki (np. "MSFT")
     * @return Złożony raport zawierający ostateczny wynik punktowy, i
     *         zidentyfikowane anomalie.
     */
    AnalysisReport analyzeStock(String ticker);
}
