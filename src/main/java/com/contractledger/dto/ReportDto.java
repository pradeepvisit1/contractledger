package com.contractledger.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class ReportDto {

    @Data
    @Builder
    public static class Response {
        private String projectName;
        private String projectType;
        private String projectLocation;
        private BigDecimal totalInvested;
        private BigDecimal totalExpenses;
        private BigDecimal totalWages;
        private BigDecimal grandExpenses;
        private BigDecimal netBalance;
        private BigDecimal materialCost;
        private BigDecimal laborCost;
        private BigDecimal machineryCost;
        private BigDecimal transportCost;
        private BigDecimal otherCost;
        private List<InvestorDto.Response> investors;
        private List<MonthlyData> monthlySummary;
    }

    @Data
    @Builder
    public static class MonthlyData {
        private String month;
        private BigDecimal amount;
    }
}
