package com.stock.screener.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "forward_estimates")
public class ForwardEstimates extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "stock_ticker", nullable = false, unique = true)
    public Stock stock;

    public BigDecimal forwardRevenueGrowth2Y;
    public BigDecimal forwardEpsGrowth2Y;
    public BigDecimal targetPrice;

    public LocalDate forecastDate; // Data kiedy pobrano prognozę
}