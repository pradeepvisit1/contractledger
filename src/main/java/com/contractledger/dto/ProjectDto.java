package com.contractledger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectDto {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String type;
        private String location;
        private String description;
        private LocalDate startDate;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String type;
        private String location;
        private String description;
        private String status;
        private String createdAt;
        private LocalDate startDate;
        private BigDecimal totalInvested;
        private BigDecimal totalExpenses;
        private BigDecimal totalWages;
        private BigDecimal grandExpenses;
        private BigDecimal balance;
    }
}
