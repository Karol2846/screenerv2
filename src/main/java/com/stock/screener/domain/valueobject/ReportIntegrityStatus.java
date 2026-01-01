package com.stock.screener.domain.valueobject;

public enum ReportIntegrityStatus {
    /** Dane tylko z Yahoo (niepełny bilans) */
    YH_PARTIAL,
    /** Pełne dane (uzupełnione z Alpha Vantage) - gotowe do Z-Score */
    COMPLETE,
    /** Próba pobrania z AV nieudana, dane niekompletne */
    STALE_MISSING_DATA
}
