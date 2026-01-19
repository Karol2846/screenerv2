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
    public BigDecimal psRatio;
    public BigDecimal forwardPeRatio;
    public BigDecimal forwardPegRatio;

    @Embedded
    public AnalystRatings analystRatings;

    @CreationTimestamp
    public LocalDate forecastDate;

    public PsRatio calculatePsRatio(BigDecimal marketCap, BigDecimal revenueTTM) {
        PsRatio ratio = PsRatio.calculate(marketCap, revenueTTM);
        this.psRatio = ratio != null ? ratio.value() : null;
        return ratio;
    }

    public ForwardPeg calculateForwardPeg() {
        ForwardPeg peg = ForwardPeg.calculate(forwardPeRatio, forwardEpsGrowth);
        this.forwardPegRatio = peg != null ? peg.value() : null;
        return peg;
    }

    public UpsidePotential calculateUpsidePotential(BigDecimal currentPrice) {
        return UpsidePotential.calculate(targetPrice, currentPrice);
    }
}