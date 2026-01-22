package com.stock.screener.domain.entity;

import com.stock.screener.domain.valueobject.AnalystRatings;
import com.stock.screener.domain.valueobject.ForwardPeg;
import com.stock.screener.domain.valueobject.PsRatio;
import com.stock.screener.domain.valueobject.UpsidePotential;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "monthly_report")
public class MonthlyReport extends PanacheEntity {

    @ManyToOne
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
    public AnalystRatings analystRatings;

    @CreationTimestamp
    public LocalDate forecastDate;

    public PsRatio calculatePsRatio(BigDecimal marketCap, BigDecimal revenueTTM) {
        this.psRatio = PsRatio.calculate(marketCap, revenueTTM);
        return this.psRatio;
    }

    public ForwardPeg calculateForwardPeg() {
        this.forwardPegRatio = ForwardPeg.calculate(forwardPeRatio, forwardEpsGrowth);
        return this.forwardPegRatio;
    }

    public UpsidePotential calculateUpsidePotential(BigDecimal currentPrice) {
        return UpsidePotential.calculate(targetPrice, currentPrice);
    }
}