package com.stock.screener.domain.entity;

import com.stock.screener.domain.kernel.ReportError;
import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.ForwardPeg;
import com.stock.screener.domain.valueobject.PsRatio;
import com.stock.screener.domain.valueobject.ReportIntegrityStatus;
import com.stock.screener.domain.valueobject.UpsidePotential;
import com.stock.screener.domain.valueobject.snapshot.MarketDataSnapshot;
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
@Table(name = "monthly_report")
public class MonthlyReport extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_ticker", nullable = false)
    public Stock stock;

    public BigDecimal forwardRevenueGrowth;
    public BigDecimal forwardEpsGrowth;
    public BigDecimal targetPrice;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ps_ratio"))
    public PsRatio psRatio;

    public BigDecimal forwardPeRatio;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "forward_peg_ratio"))
    public ForwardPeg forwardPegRatio;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "upside_potential"))
    public UpsidePotential upsidePotential;

    @Embedded
    public AnalystRatings analystRatings;

    @Enumerated(EnumType.STRING)
    public ReportIntegrityStatus integrityStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    public Set<ReportError> calculationErrors = new HashSet<>();

    @CreationTimestamp
    public LocalDate forecastDate;

    @UpdateTimestamp
    public LocalDateTime updatedAt;

    public void updateMetrics(MarketDataSnapshot snapshot) {
        this.calculationErrors.clear();

        // Step 1: Update raw fields from snapshot (Source of Truth)
        updateRawFields(snapshot);

        // Step 2: Recalculate complex Value Objects
        recalculatePsRatio(snapshot);
        recalculateForwardPeg(snapshot);
        recalculateUpsidePotential(snapshot);

        // Step 3: Determine integrity status
        updateIntegrityStatus();
    }

    void updateRawFields(MarketDataSnapshot snapshot) {
        this.forwardPeRatio = snapshot.forwardPeRatio();
        this.forwardEpsGrowth = snapshot.forwardEpsGrowth();
        this.forwardRevenueGrowth = snapshot.forwardRevenueGrowth();
        this.targetPrice = snapshot.targetPrice();
        this.analystRatings = snapshot.analystRatings();
    }

    void recalculatePsRatio(MarketDataSnapshot snapshot) {
        PsRatio.compute(snapshot)
                .onSuccess(ps -> this.psRatio = ps)
                .onFailure(failure -> {
                    this.psRatio = null;
                    this.calculationErrors.add(fromFailure(PS_RATIO, failure));
                });
    }

    void recalculateForwardPeg(MarketDataSnapshot snapshot) {
        ForwardPeg.compute(snapshot)
                .onSuccess(peg -> this.forwardPegRatio = peg)
                .onFailure(failure -> {
                    this.forwardPegRatio = null;
                    this.calculationErrors.add(fromFailure(FORWARD_PEG, failure));
                });
    }

    void recalculateUpsidePotential(MarketDataSnapshot snapshot) {
        UpsidePotential.compute(snapshot)
                .onSuccess(up -> this.upsidePotential = up)
                .onFailure(failure -> {
                    this.upsidePotential = null;
                    this.calculationErrors.add(fromFailure(UPSIDE_POTENTIAL, failure));
                });
    }

    private void updateIntegrityStatus() {
        boolean pricingComplete = hasPricingData();
        boolean fundamentalsComplete = hasFundamentalData();
        boolean hybridComplete = forwardPegRatio != null;

        if (pricingComplete && fundamentalsComplete && hybridComplete) {
            this.integrityStatus = ReportIntegrityStatus.READY_FOR_ANALYSIS;
        } else if (pricingComplete && !fundamentalsComplete) {
            this.integrityStatus = ReportIntegrityStatus.PRICING_DATA_COLLECTED;
        } else if (fundamentalsComplete && !pricingComplete) {
            this.integrityStatus = ReportIntegrityStatus.FUNDAMENTALS_COLLECTED;
        } else {
            this.integrityStatus = ReportIntegrityStatus.MISSING_DATA;
        }
    }

    /**
     * Checks if fundamental data (analyst ratings, growth forecasts) is available.
     */
    private boolean hasFundamentalData() {
        return analystRatings != null &&
                forwardRevenueGrowth != null &&
                forwardEpsGrowth != null;
    }

    /**
     * Checks if pricing data (P/S ratio, upside potential) is computed.
     * Note: ForwardPeg is EXCLUDED because it's a hybrid metric (requires fundamental growth data).
     */
    private boolean hasPricingData() {
        return psRatio != null
                && upsidePotential != null;
    }
}