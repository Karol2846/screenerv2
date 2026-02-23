package com.stock.screener.domain.entity;

import com.stock.screener.domain.valueobject.MarketData;
import com.stock.screener.domain.valueobject.Sector;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Lightweight aggregate representing a publicly listed company.
 * Contains only basic identification and market data.
 * Reports ({@link MonthlyReport}, {@link QuarterlyReport}) are accessible
 * through their own repositories/Active Record, not via navigation from Stock.
 * </p>
 */
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

    public Stock() {}
}

//TODO: new problem to think of:
// think about how to merge alpha vantage & yhFinance api calls in monthlyReport - to now waste requests from api's
// MonthlyReportsnapshot requires some data from YhFinance & some from alphaVAntage.
// It's data will be fetched once a month.
// but alphaVantage api's data also needs to be fetched once every three months - for quaterly report.
// Maybe the solution will be to gave data from persist log if current data was already fettched