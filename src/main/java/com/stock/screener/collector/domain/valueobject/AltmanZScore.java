package com.stock.screener.collector.domain.valueobject;

import com.stock.screener.collector.domain.service.AltmanScoreCalculator;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Value Object representing Altman Z-Score.
 * <p>
 * This is a simple data holder (fact). It does NOT contain calculation logic
 * nor risk interpretation methods (isInGreyZone, isSafe, etc.).
 * <p>
 * Calculation is performed by {@link AltmanScoreCalculator}.
 * Risk interpretation belongs to a separate analysis module.
 */
@Embeddable
public record AltmanZScore(BigDecimal value) implements FinancialMetric {
}

