package com.stock.screener.domain.entity;

import com.stock.screener.domain.valueobject.MarketData;
import com.stock.screener.domain.valueobject.Sector;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Lekki agregat reprezentujący spółkę giełdową.
 * <p>
 * Zawiera tylko podstawowe dane identyfikacyjne i rynkowe.
 * Raporty ({@link MonthlyReport}, {@link QuarterlyReport}) są dostępne
 * przez ich własne repozytoria/Active Record, a nie przez nawigację z Stock.
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