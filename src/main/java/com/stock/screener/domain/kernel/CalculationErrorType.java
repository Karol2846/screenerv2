package com.stock.screener.domain.kernel;

public enum CalculationErrorType {

    /** Brak wymaganych danych wejściowych (null) */
    MISSING_DATA,

    /** Dzielenie przez zero lub wartość zerowa w mianowniku */
    DIVISION_BY_ZERO,

    /** Wartości poza dozwolonym zakresem */
    INVALID_RANGE,

    /** Wartość ujemna tam, gdzie oczekiwano dodatniej */
    NEGATIVE_VALUE
}
