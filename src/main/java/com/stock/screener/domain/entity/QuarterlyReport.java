package com.stock.screener.domain.entity;

import com.stock.screener.domain.valueobject.AltmanZScore;
import com.stock.screener.domain.valueobject.InterestCoverageRatio;
import com.stock.screener.domain.valueobject.QuickRatio;
import com.stock.screener.domain.valueobject.ReportIntegrityStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quarterly_report")
public class QuarterlyReport extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_ticker", nullable = false)
    public Stock stock;

    @Column(nullable = false)
    public LocalDate fiscalDateEnding;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ReportIntegrityStatus integrityStatus;

    // --- P&L ---
    public BigDecimal totalRevenue;
    public BigDecimal netIncome;

    // --- Balance Sheet ---
    public BigDecimal totalDebt;
    public BigDecimal totalAssets;

    // --- Cash Flow ---
    public BigDecimal operatingCashFlow;

    // --- Calculated Ratios (stored as BigDecimal for DB) ---
    public BigDecimal quickRatio;
    public BigDecimal interestCoverageRatio;
    public BigDecimal altmanZScore;

    @CreationTimestamp
    public LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;

    /**
     * Calculates and stores Quick Ratio.
     */
    public QuickRatio calculateQuickRatio(BigDecimal totalCurrentAssets,
                                          BigDecimal inventory,
                                          BigDecimal totalCurrentLiabilities) {
        QuickRatio ratio = QuickRatio.calculate(totalCurrentAssets, inventory, totalCurrentLiabilities);
        this.quickRatio = ratio != null ? ratio.value() : null;
        return ratio;
    }

    /**
     * Calculates and stores Interest Coverage Ratio.
     */
    public InterestCoverageRatio calculateInterestCoverageRatio(BigDecimal ebit, BigDecimal interestExpense) {
        InterestCoverageRatio ratio = InterestCoverageRatio.calculate(ebit, interestExpense);
        this.interestCoverageRatio = ratio != null ? ratio.value() : null;
        return ratio;
    }

    /**
     * Calculates and stores Altman Z''-Score.
     */
    public AltmanZScore calculateAltmanZScore(BigDecimal totalCurrentAssets,
                                              BigDecimal totalCurrentLiabilities,
                                              BigDecimal retainedEarnings,
                                              BigDecimal ebit,
                                              BigDecimal totalShareholderEquity,
                                              BigDecimal totalLiabilities) {
        AltmanZScore score = AltmanZScore.calculate(
                totalCurrentAssets,
                totalCurrentLiabilities,
                this.totalAssets,
                retainedEarnings,
                ebit,
                totalShareholderEquity,
                totalLiabilities);
        this.altmanZScore = score != null ? score.value() : null;
        return score;
    }

    public QuickRatio getQuickRatioVO() {
        return quickRatio != null ? new QuickRatio(quickRatio) : null;
    }

    /**
     * Returns Value Object with domain logic for ICR interpretation.
     */
    public InterestCoverageRatio getInterestCoverageRatioVO() {
        return interestCoverageRatio != null ? new InterestCoverageRatio(interestCoverageRatio) : null;
    }

    /**
     * Returns Value Object with domain logic for Z-Score interpretation.
     */
    public AltmanZScore getAltmanZScoreVO() {
        return altmanZScore != null ? new AltmanZScore(altmanZScore) : null;
    }
}