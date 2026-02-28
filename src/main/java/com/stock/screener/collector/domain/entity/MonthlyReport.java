package com.stock.screener.collector.domain.entity;

import com.stock.screener.collector.domain.kernel.ReportError;
import com.stock.screener.collector.domain.valueobject.AnalystRatings;
import com.stock.screener.collector.domain.valueobject.ForwardPeg;
import com.stock.screener.collector.domain.valueobject.PsRatio;
import com.stock.screener.collector.domain.valueobject.ReportIntegrityStatus;
import com.stock.screener.common.Sector;
import com.stock.screener.collector.domain.valueobject.UpsidePotential;
import com.stock.screener.collector.domain.valueobject.snapshot.MarketDataSnapshot;
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

import static com.stock.screener.collector.domain.kernel.MetricType.*;
import static com.stock.screener.collector.domain.kernel.ReportError.fromFailure;

@Entity
@Table(name = "monthly_report")
public class MonthlyReport extends PanacheEntity {

    @Column(name = "stock_ticker", length = 10, nullable = false)
    public String ticker;

    @Enumerated(EnumType.STRING)
    public Sector sector;

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
        if (snapshot.forwardPeRatio() != null)
            this.forwardPeRatio = snapshot.forwardPeRatio();
        if (snapshot.forwardEpsGrowth() != null)
            this.forwardEpsGrowth = snapshot.forwardEpsGrowth();
        if (snapshot.forwardRevenueGrowth() != null)
            this.forwardRevenueGrowth = snapshot.forwardRevenueGrowth();
        if (snapshot.targetPrice() != null)
            this.targetPrice = snapshot.targetPrice();
        if (snapshot.analystRatings() != null)
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

//TODO: new problem to think of:
// think about how to merge alpha vantage & yhFinance api calls in monthlyReport - to now waste requests from api's
// MonthlyReportsnapshot requires some data from YhFinance & some from alphaVAntage.
// It's data will be fetched once a month.
// but alphaVantage api's data also needs to be fetched once every three months - for quaterly report.
// Maybe the solution will be to gave data from persist log if current data was already fettched