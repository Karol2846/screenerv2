package com.stock.screener.domain.entity;

import com.stock.screener.domain.valueobject.AnalystRatings;
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

    @Embedded
    public AnalystRatings analystRatings;

    @CreationTimestamp
    public LocalDate forecastDate; // Data kiedy pobrano prognozę
}