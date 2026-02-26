package com.stock.screener.collector.adapter.out.web.alphavantage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Handles AlphaVantage quirks where numeric fields can arrive as "None", "-", or "".
 * Maps those to null instead of blowing up.
 */
 class NullSafeBigDecimalDeserializer extends NumberDeserializers.BigDecimalDeserializer {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.isBlank() || "None".equalsIgnoreCase(text) || "-".equals(text)) {
            return null;
        }
        return super.deserialize(p, ctxt);
    }
}
