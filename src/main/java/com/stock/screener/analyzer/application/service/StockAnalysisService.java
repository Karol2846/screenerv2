package com.stock.screener.analyzer.application.service;

import com.stock.screener.analyzer.application.port.in.AnalyzeStockUseCase;
import com.stock.screener.domain.entity.Stock;
import com.stock.screener.domain.valueobject.AnalysisReport;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StockAnalysisService implements AnalyzeStockUseCase {

    @Override
    public AnalysisReport analyzeStock(String ticker) {
        log.info("Starting analysis and scoring for: {}", ticker);

        List<String> anomalies = new ArrayList<>();
        int score = 0;
        int maxPossible = 95;
        String rejectReason = null;

        return AnalysisReport.builder()
                .ticker(ticker)
                .totalScore(score)
                .maxPossibleScore(maxPossible)
                .rejectReason(rejectReason)
                .anomalies(anomalies)
                .metricErrors(new HashSet<>())
                .build();
    }
}
