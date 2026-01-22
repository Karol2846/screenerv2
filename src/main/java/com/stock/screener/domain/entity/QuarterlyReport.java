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
    public ReportIntegrityStatus integrityStatus;

    // --- P&L ---
    public BigDecimal totalRevenue;
    public BigDecimal netIncome;

    // --- Balance Sheet ---
    public BigDecimal totalDebt;
    public BigDecimal totalAssets;

    // --- Cash Flow ---
    public BigDecimal operatingCashFlow;

    // --- Calculated Ratios (Value Objects as @Embeddable records) ---
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quick_ratio"))
    public QuickRatio quickRatio;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "interest_coverage_ratio"))
    public InterestCoverageRatio interestCoverageRatio;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "altman_z_score"))
    public AltmanZScore altmanZScore;

    @CreationTimestamp
    public LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;

    public QuickRatio calculateQuickRatio(BigDecimal totalCurrentAssets,
                                          BigDecimal inventory,
                                          BigDecimal totalCurrentLiabilities) {
        this.quickRatio = QuickRatio.calculate(totalCurrentAssets, inventory, totalCurrentLiabilities);
        return quickRatio;
    }

    public InterestCoverageRatio calculateInterestCoverageRatio(BigDecimal ebit, BigDecimal interestExpense) {
        this.interestCoverageRatio = InterestCoverageRatio.calculate(ebit, interestExpense);
        return this.interestCoverageRatio;
    }

    public AltmanZScore calculateAltmanZScore(BigDecimal totalCurrentAssets,
                                              BigDecimal totalCurrentLiabilities,
                                              BigDecimal retainedEarnings,
                                              BigDecimal ebit,
                                              BigDecimal totalShareholderEquity,
                                              BigDecimal totalLiabilities) {
        this.altmanZScore = AltmanZScore.calculate(
                totalCurrentAssets,
                totalCurrentLiabilities,
                this.totalAssets,
                retainedEarnings,
                ebit,
                totalShareholderEquity,
                totalLiabilities);
        return this.altmanZScore;
    }
}