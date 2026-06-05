package com.stock.screener.collector.domain.valueobject;

public enum InterestCoverageStatus {
    /** EBIT ≥ 0 and interest expense > 0 — ratio is meaningful. */
    COVERED,
    /** Interest expense is zero or absent — no debt service obligation. Positive signal. */
    NO_DEBT,
    /** EBIT < 0 — operating loss; ratio would be numerically misleading. */
    OPERATING_LOSS
}
