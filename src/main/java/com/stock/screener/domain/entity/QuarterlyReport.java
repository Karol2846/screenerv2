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
import java.util.List;

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
    public List<ReportError> calculationErrors = new ArrayList<>();

    @CreationTimestamp
    public LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;

    //TODO: idziemy w dobrą stronę z tym podejściem, ale jeszcze sporo pracy
    // - trzeba je zastosować la reszty metryk i encji - poczytaj to: https://gemini.google.com/app/68ea4aa143b25bd4?hl=pl
    // - trzeba wprowadzić dodatkowe wartości do integroty Status
    // Po implementacji pamiętaj o porządnych testach jednostkowych!

    // --- Business Methods with Result Pattern ---
    public void updateQuickRatio(BigDecimal totalCurrentAssets,
                                 BigDecimal inventory,
                                 BigDecimal totalCurrentLiabilities) {
        CalculationResult<QuickRatio> result = QuickRatio.compute(
                totalCurrentAssets,
                inventory,
                totalCurrentLiabilities
        );

        result
                .onSuccess(qr -> this.quickRatio = qr)
                .onFailure(failure -> {
                    this.quickRatio = null;
                    this.calculationErrors.add(ReportError.fromFailure("QuickRatio", failure));
                });

        updateIntegrityStatus();
    }

    /**
     * Aktualizuje wszystkie metryki na podstawie danych wejściowych.
     * Błędy są logowane, nie rzucane.
     */
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

        // Interest Coverage Ratio (zachowane dla kompatybilności - TODO: migracja do Result Pattern)
        this.interestCoverageRatio = InterestCoverageRatio.calculate(ebit, interestExpense);

        // Altman Z-Score (zachowane dla kompatybilności - TODO: migracja do Result Pattern)
        this.altmanZScore = AltmanZScore.calculate(
                totalCurrentAssets,
                totalCurrentLiabilities,
                this.totalAssets,
                retainedEarnings,
                ebit,
                totalShareholderEquity,
                totalLiabilities
        );

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
                this.integrityStatus = ReportIntegrityStatus.YH_PARTIAL;
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

    // --- Legacy methods (deprecated) ---

    /**
     * @deprecated Użyj {@link #updateQuickRatio} z Result Pattern.
     */
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
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