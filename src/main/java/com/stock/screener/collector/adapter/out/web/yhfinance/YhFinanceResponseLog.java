package com.stock.screener.collector.adapter.out.web.yhfinance;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "yh_finance_response_log")
class YhFinanceResponseLog extends PanacheEntity {

    @Column(nullable = false, length = 10)
    public String ticker;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public LocalDateTime requestTimestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    public String rawResponse;

    public YhFinanceResponseLog() {}

    YhFinanceResponseLog(String ticker, String rawResponse) {
        this.ticker = ticker;
        this.rawResponse = rawResponse;
    }
}
