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
        log.info("Rozpoczęcie analizy i scoringu dla: {}", ticker);

        // TODO: 1. Pobierz aktualne dane o spółce
        // (Stock stock = Stock.findById(ticker); if null -> rzuć wyjątek lub zainicjuj
        // pobieranie)

        // TODO: 2. Przejdź punktowanie dla: (wg work_plan.md)
        // - "Bezpieczeństwo" (max 12 pkt, Odrzuca przy: quickRatio < 0.8, altmanZScore
        // < 1.8)
        // - "Wzrost spółki" (max 28 pkt)
        // - "Wycena spółki" (max 30 pkt)
        // - "Ocena analityków" (max 25 pkt)

        // Przykładowy szkielet:
        List<String> anomalies = new ArrayList<>();
        int score = 0;
        int maxPossible = 95; // Przykładowo, maksymalna pula bazowa (zależnie od dostępności)
        String rejectReason = null;

        // Tutaj logika z domain/services np. ScoringEngine.scoreSafety(stock),
        // ScoringEngine.scoreGrowth(stock)...

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
