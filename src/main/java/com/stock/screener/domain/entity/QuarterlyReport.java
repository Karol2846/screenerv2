package com.stock.screener.domain.entity;

import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.kernel.ReportError;
import com.stock.screener.domain.valueobject.AltmanZScore;
import com.stock.screener.domain.valueobject.snapshoot.FinancialDataSnapshot;
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
import java.util.HashSet;
import java.util.Set;

import static com.stock.screener.domain.kernel.MetricType.*;
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

    public void updateMetrics(FinancialDataSnapshot snapshot) {
        this.calculationErrors.clear();

        FinancialDataSnapshot enrichedSnapshot = enrichWithEntityData(snapshot);

        updateQuickRatio(enrichedSnapshot);
        updateInterestCoverageRatio(enrichedSnapshot);
        updateAltmanZScore(enrichedSnapshot);

        updateIntegrityStatus();
    }

    private FinancialDataSnapshot enrichWithEntityData(FinancialDataSnapshot snapshot) {
        return FinancialDataSnapshot.builder()
                .totalCurrentAssets(snapshot.totalCurrentAssets())
                .totalCurrentLiabilities(snapshot.totalCurrentLiabilities())
                .totalAssets(snapshot.totalAssets() != null ? snapshot.totalAssets() : this.totalAssets)
                .totalLiabilities(snapshot.totalLiabilities())
                .retainedEarnings(snapshot.retainedEarnings())
                .ebit(snapshot.ebit())
                .interestExpense(snapshot.interestExpense())
                .totalShareholderEquity(snapshot.totalShareholderEquity())
                .inventory(snapshot.inventory())
                .build();
    }

    void updateQuickRatio(FinancialDataSnapshot snapshot) {
        CalculationResult<QuickRatio> result = QuickRatio.compute(snapshot);

        result.onSuccess(qr -> this.quickRatio = qr)
                .onFailure(failure -> {
                    this.quickRatio = null;
                    this.calculationErrors.add(fromFailure(QUICK_RATIO, failure));
                });
        updateIntegrityStatus();
    }

    void updateInterestCoverageRatio(FinancialDataSnapshot snapshot) {
        CalculationResult<InterestCoverageRatio> result = InterestCoverageRatio.compute(snapshot);

        result.onSuccess(icr -> this.interestCoverageRatio = icr)
                .onFailure(failure -> {
                    this.interestCoverageRatio = null;
                    this.calculationErrors.add(fromFailure(INTEREST_COVERAGE_RATIO, failure));
                });

        updateIntegrityStatus();
    }

    void updateAltmanZScore(FinancialDataSnapshot snapshot) {
        CalculationResult<AltmanZScore> result = AltmanZScore.compute(snapshot);

        result
                .onSuccess(az -> this.altmanZScore = az)
                .onFailure(failure -> {
                    this.altmanZScore = null;
                    this.calculationErrors.add(fromFailure(ALTMAN_Z_SCORE, failure));
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
                this.integrityStatus = ReportIntegrityStatus.AV_FETCHED_COMPLETED;
            }
        } else {
            this.integrityStatus = ReportIntegrityStatus.MISSING_DATA;
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
}