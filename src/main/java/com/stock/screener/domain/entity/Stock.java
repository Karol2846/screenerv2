package com.stock.screener.domain.entity;

import com.stock.screener.domain.valueobject.MarketData;
import com.stock.screener.domain.valueobject.Sector;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    public List<ForwardEstimates> currentEstimates;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<QuarterlyReport> quarterlyReports = new ArrayList<>();
}