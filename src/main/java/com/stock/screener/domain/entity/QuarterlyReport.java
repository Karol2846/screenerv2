package com.stock.screener.domain.entity;

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

    // --- P&L (Source: YH + AV fallback) ---
    public BigDecimal totalRevenue;
    public BigDecimal netIncome;
    public BigDecimal ebit;            // Critical for interestConverageRatio
    public BigDecimal interestExpense;

    // --- Balance Sheet (Source: AV mainly) ---
    public BigDecimal totalAssets;
    public BigDecimal totalCurrentAssets;
    public BigDecimal totalLiabilities;
    public BigDecimal totalCurrentLiabilities;
    public BigDecimal retainedEarnings;

    // --- Cash Flow ---
    public BigDecimal operatingCashFlow;

    @CreationTimestamp
    public LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;

    //TODO: funkcja do wyliczania altman-Z score, wzór w pliku data_collected.md
}