package com.stock.screener.adapter.web.out.model.quotesummary;

import com.stock.screener.adapter.web.out.model.FormattedValue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Profil firmy - informacje o firmie, adresie, oficerach itp.
 */
@Builder
@Jacksonized
public record AssetProfile(
        String address1,
        String address2,
        String city,
        String state,
        String zip,
        String country,
        String phone,
        String website,
        String industry,
        String sector,
        String longBusinessSummary,
        Integer fullTimeEmployees,
        List<CompanyOfficer> companyOfficers,
        Integer auditRisk,
        Integer boardRisk,
        Integer compensationRisk,
        Integer shareHolderRightsRisk,
        Integer overallRisk,
        Long governanceEpochDate,
        Long compensationAsOfEpochDate,
        Integer maxAge
) {

    @Builder
    @Jacksonized
    public record CompanyOfficer(
            String name,
            String title,
            Integer age,
            Integer yearBorn,
            Integer fiscalYear,
            FormattedValue totalPay,
            FormattedValue exercisedValue,
            FormattedValue unexercisedValue,
            Integer maxAge
    ) {
    }
}

