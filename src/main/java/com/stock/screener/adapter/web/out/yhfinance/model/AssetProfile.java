package com.stock.screener.adapter.web.out.yhfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetProfile(
        String address1,
        String city,
        String state,
        String zip,
        String country,
        String phone,
        String website,
        String industry,
        String industryKey,
        String industryDisp,
        String sector,
        String sectorKey,
        String sectorDisp,
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
        String irWebsite,
        Integer maxAge
) {
}

