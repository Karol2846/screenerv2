package com.stock.screener.domain.entity;

import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.kernel.ReportError;
import com.stock.screener.domain.valueobject.AltmanZScore;
import com.stock.screener.domain.valueobject.InterestCoverageRatio;
import com.stock.screener.domain.valueobject.QuickRatio;
import com.stock.screener.domain.valueobject.ReportIntegrityStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.stock.screener.domain.kernel.ReportError.fromFailure;

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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quick_ratio"))
    public QuickRatio quickRatio;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "interest_coverage_ratio"))
    public InterestCoverageRatio interestCoverageRatio;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "altman_z_score"))
    public AltmanZScore altmanZScore;

    // --- Error Logging (JSONB) ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    public Set<ReportError> calculationErrors = new HashSet<>();

    @CreationTimestamp
    public LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;

    public void updateMetrics(BigDecimal totalCurrentAssets,
                              BigDecimal inventory,
                              BigDecimal totalCurrentLiabilities,
                              BigDecimal ebit,
                              BigDecimal interestExpense,
                              BigDecimal retainedEarnings,
                              BigDecimal totalShareholderEquity,
                              BigDecimal totalLiabilities) {
        this.calculationErrors.clear();

        updateQuickRatio(totalCurrentAssets, inventory, totalCurrentLiabilities);
        updateInterestCoverageRatio(ebit, interestExpense);
        updateAltmanZScore(totalCurrentAssets, totalCurrentLiabilities, retainedEarnings,
                ebit, totalShareholderEquity, totalLiabilities);

        updateIntegrityStatus();
    }

    void updateQuickRatio(BigDecimal totalCurrentAssets,
                                 BigDecimal inventory,
                                 BigDecimal totalCurrentLiabilities) {
        CalculationResult<QuickRatio> result = QuickRatio.compute(
                totalCurrentAssets,
                inventory,
                totalCurrentLiabilities);

        result.onSuccess(qr -> this.quickRatio = qr)
                .onFailure(failure -> {
                    this.quickRatio = null;
                    this.calculationErrors.add(fromFailure(QuickRatio.METRIC_NAME, failure));
                });
        updateIntegrityStatus();
    }

    void updateInterestCoverageRatio(BigDecimal ebit, BigDecimal interestExpense) {
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(
                ebit,
                interestExpense
        );

        result.onSuccess(icr -> this.interestCoverageRatio = icr)
                .onFailure(failure -> {
                    this.interestCoverageRatio = null;
                    this.calculationErrors.add(fromFailure(InterestCoverageRatio.METRIC_NAME, failure));
                });

        updateIntegrityStatus();
    }

    void updateAltmanZScore(BigDecimal totalCurrentAssets,
                                    BigDecimal totalCurrentLiabilities,
                                    BigDecimal retainedEarnings,
                                    BigDecimal ebit,
                                    BigDecimal totalShareholderEquity,
                                    BigDecimal totalLiabilities) {
        CalculationResult<AltmanZScore> result = AltmanZScore.compute(
                totalCurrentAssets,
                totalCurrentLiabilities,
                this.totalAssets,
                retainedEarnings,
                ebit,
                totalShareholderEquity,
                totalLiabilities);

        result
                .onSuccess(az -> this.altmanZScore = az)
                .onFailure(failure -> {
                    this.altmanZScore = null;
                    this.calculationErrors.add(fromFailure(AltmanZScore.METRIC_NAME, failure));
                });

        updateIntegrityStatus();
    }

    /**
     * Aktualizuje status integralności na podstawie obecności błędów.
     */
    private void updateIntegrityStatus() {
        if (calculationErrors.isEmpty()) {
            if (isComplete()) {
                this.integrityStatus = ReportIntegrityStatus.COMPLETE;
            } else {
                this.integrityStatus = ReportIntegrityStatus.AV_FETCHED;
            }
        } else {
            this.integrityStatus = ReportIntegrityStatus.STALE_MISSING_DATA;
        }
    }

    /**
     * Sprawdza czy raport ma kompletne dane do analizy.
     */
    private boolean isComplete() {
        return quickRatio != null
                && interestCoverageRatio != null
                && altmanZScore != null;
    }

    /**
     * Czy raport zawiera błędy kalkulacji?
     */
    public boolean hasErrors() {
        return !calculationErrors.isEmpty();
    }

    /**
     * Dodaje pojedynczy błąd do listy.
     */
    public void addError(ReportError error) {
        this.calculationErrors.add(error);
        updateIntegrityStatus();
    }
}