package com.stock.screener.collector.adapter.out.web.alphavantage.model;

/**
 * Common contract implemented by all Alpha Vantage response DTOs.
 * AV signals errors via HTTP 200 with one of these JSON fields set instead of the expected data.
 *
 * <ul>
 *   <li>{@code "Information"} — daily quota exhaustion (current, 25 req/day on free tier)</li>
 *   <li>{@code "Note"} — per-minute throttle (older format)</li>
 *   <li>{@code "Error Message"} — invalid symbol or malformed call</li>
 * </ul>
 */
public interface AlphaVantageResponse {
    String note();
    String information();
    String errorMessage();
}
