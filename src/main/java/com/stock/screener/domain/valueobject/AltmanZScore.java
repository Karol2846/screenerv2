package com.stock.screener.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Value Object representing Altman Z-Score.
 * <p>
 * This is a simple data holder (fact). It does NOT contain calculation logic
 * nor risk interpretation methods (isInGreyZone, isSafe, etc.).
 * <p>
 * Calculation is performed by {@link com.stock.screener.domain.service.AltmanScoreCalculator}.
 * Risk interpretation belongs to a separate analysis module.
 */
@Embeddable
public record AltmanZScore(BigDecimal value) implements FinancialMetric {
}

