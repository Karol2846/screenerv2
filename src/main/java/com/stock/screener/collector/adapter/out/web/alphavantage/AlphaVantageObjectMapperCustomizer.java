package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

import java.math.BigDecimal;

@Singleton
public class AlphaVantageObjectMapperCustomizer implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule("AlphaVantageModule");
        module.addDeserializer(BigDecimal.class, new NullSafeBigDecimalDeserializer());
        objectMapper.registerModule(module);
    }
}
