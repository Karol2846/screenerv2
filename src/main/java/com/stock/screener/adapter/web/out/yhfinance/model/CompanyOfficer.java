package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyOfficer(
        Integer maxAge,
        String name,
        Integer age,
        String title,
        Integer yearBorn,
        Integer fiscalYear,
        RawFmtValue totalPay,
        RawFmtValue exercisedValue,
        RawFmtValue unexercisedValue
) {
}
