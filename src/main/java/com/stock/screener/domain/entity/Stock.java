package com.stock.screener.domain.entity;

import com.stock.screener.domain.valueobject.MarketData;
import com.stock.screener.domain.valueobject.Sector;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Table(name = "stock")
public class Stock extends PanacheEntityBase {

    @Id
    @Column(length = 10, nullable = false)
    public String ticker;

    @Enumerated(EnumType.STRING)
    public Sector sector;

    @Embedded
    public MarketData marketData;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<MonthlyReport> currentEstimates;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<QuarterlyReport> quarterlyReports = new ArrayList<>();

    public Stock() {}

    public Stock(String ticker) {
        this.ticker = ticker;
    }


    public static Stock findOrCreate(String id) {
        return (Stock) findByIdOptional(id).orElse(new Stock(id));
    }

    public void updateMarketData(MarketData marketData) {
        log.info("Updating market data for ticker: {}", ticker);
    }
}