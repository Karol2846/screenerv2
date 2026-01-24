package com.stock.screener.domain.entity;

import com.stock.screener.domain.kernel.CalculationResult;
import com.stock.screener.domain.kernel.ReportError;
import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.ForwardPeg;
import com.stock.screener.domain.valueobject.PsRatio;
import com.stock.screener.domain.valueobject.ReportIntegrityStatus;
import com.stock.screener.domain.valueobject.UpsidePotential;
import com.stock.screener.domain.valueobject.snapshoot.MarketDataSnapshot;
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

    //TODO: przejżyj te klasy czy nie da się czegoś poprawić
    public void updateMetrics(MarketDataSnapshot snapshot) {
        this.calculationErrors.clear();
        //TODO: snapshoot musi być zaktualizowany przed wywołaniem tej metody - nie powinien forwardEpsGrowth
        // pola revenue, eps i anlaystRating będą brane z yhFinance <- to musi być pierwsze
        updatePsRatio(snapshot);
        updateForwardPeg(snapshot);
        updateUpsidePotential(snapshot);

        updateIntegrityStatus();
    }

    void updatePsRatio(MarketDataSnapshot snapshot) {
        CalculationResult<PsRatio> result = PsRatio.compute(snapshot);

        result.onSuccess(ps -> this.psRatio = ps)
                .onFailure(failure -> {
                    this.psRatio = null;
                    this.calculationErrors.add(fromFailure(PS_RATIO, failure));
                });
    }

    void updateForwardPeg(MarketDataSnapshot snapshot) {
        CalculationResult<ForwardPeg> result = ForwardPeg.compute(snapshot);

        result.onSuccess(peg -> this.forwardPegRatio = peg)
                .onFailure(failure -> {
                    this.forwardPegRatio = null;
                    this.calculationErrors.add(fromFailure(FORWARD_PEG, failure));
                });
    }

    void updateUpsidePotential(MarketDataSnapshot snapshot) {
        CalculationResult<UpsidePotential> result = UpsidePotential.compute(snapshot);

        result.onSuccess(up -> this.upsidePotential = up)
                .onFailure(failure -> {
                    this.upsidePotential = null;
                    this.calculationErrors.add(fromFailure(UPSIDE_POTENTIAL, failure));
                });
    }

    private void updateIntegrityStatus() {
        if (!calculationErrors.isEmpty()) {
            if (isAVFetchingCompleted() && isYHFinanceFetchingCompleted()) {
                this.integrityStatus = ReportIntegrityStatus.COMPLETE;
            } else if(isAVFetchingCompleted()) {
                this.integrityStatus = ReportIntegrityStatus.AV_FETCHED_COMPLETED;
            } else if (isYHFinanceFetchingCompleted()){
                this.integrityStatus = ReportIntegrityStatus.YH_FETCHED_COMPLETED;
            } else {
                this.integrityStatus = ReportIntegrityStatus.MISSING_DATA;
            }
        } else {
            this.integrityStatus = ReportIntegrityStatus.MISSING_DATA;
        }
    }

    private boolean isYHFinanceFetchingCompleted() {
        return analystRatings != null &&
                forwardRevenueGrowth != null &&
                forwardEpsGrowth != null;
    }

    private boolean isAVFetchingCompleted() {
        return psRatio != null
                && forwardPegRatio != null
                && upsidePotential != null;
    }
}