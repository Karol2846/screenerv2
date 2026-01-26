package com.stock.screener.domain.entity;

import com.stock.screener.domain.kernel.ReportError;
import com.stock.screener.domain.valueobject.AltmanZScore;
import com.stock.screener.domain.valueobject.Sector;
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
import static com.stock.screener.domain.kernel.ReportError.fromSkipped;

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

    public void updateMetrics(FinancialDataSnapshot snapshot, Sector sector) {
        this.calculationErrors.clear();

        FinancialDataSnapshot enrichedSnapshot = enrichWithEntityData(snapshot);

        recalculateQuickRatio(enrichedSnapshot);
        recalculateInterestCoverageRatio(enrichedSnapshot);
        recalculateAltmanZScore(enrichedSnapshot, sector);

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
                .totalRevenue(this.totalRevenue) //FIXME: totalRevenue is onlyrevenue from given financialQuater. Change this to cumulative revenue (from 4 quarters)
                .build();
    }

    private void recalculateQuickRatio(FinancialDataSnapshot snapshot) {
        QuickRatio.compute(snapshot)
                .onSuccess(qr -> this.quickRatio = qr)
                .onFailure(failure -> {
                    this.quickRatio = null;
                    this.calculationErrors.add(fromFailure(QUICK_RATIO, failure));
                });
    }

    private void recalculateInterestCoverageRatio(FinancialDataSnapshot snapshot) {
        InterestCoverageRatio.compute(snapshot)
                .onSuccess(icr -> this.interestCoverageRatio = icr)
                .onFailure(failure -> {
                    this.interestCoverageRatio = null;
                    this.calculationErrors.add(fromFailure(INTEREST_COVERAGE_RATIO, failure));
                });
    }

    private void recalculateAltmanZScore(FinancialDataSnapshot snapshot, Sector sector) {
        AltmanZScore.compute(snapshot, sector)
                .onSuccess(az -> this.altmanZScore = az)
                .onFailure(failure -> {
                    this.altmanZScore = null;
                    this.calculationErrors.add(fromFailure(ALTMAN_Z_SCORE, failure));
                })
                .onSkipped(skipped -> {
                    this.altmanZScore = null;
                    this.calculationErrors.add(fromSkipped(ALTMAN_Z_SCORE, skipped));
                });
    }

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

    private boolean isComplete() {
        return quickRatio != null
                && interestCoverageRatio != null
                && altmanZScore != null;
    }
}