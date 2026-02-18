package com.stock.screener.adapter.web.out.alphavantage;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "alpha_vantage_response_log")
public class AlphaVantageResponseLog extends PanacheEntity {

    @Column(nullable = false, length = 10)
    public String ticker;

    @Column(nullable = false, length = 50)
    public String functionName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public LocalDateTime requestTimestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    public String rawResponse;

    public AlphaVantageResponseLog() {}

    public AlphaVantageResponseLog(String ticker, String functionName, String rawResponse) {
        this.ticker = ticker;
        this.functionName = functionName;
        this.rawResponse = rawResponse;
    }
}
