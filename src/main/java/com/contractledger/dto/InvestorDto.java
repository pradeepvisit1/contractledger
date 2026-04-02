package com.contractledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestorDto {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String phone;
        @NotNull
        @Positive
        private BigDecimal amount;
        private LocalDate investmentDate;
        private String notes;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String phone;
        private BigDecimal amount;
        private LocalDate investmentDate;
        private String notes;
        private String createdAt;
        private double sharePercent;
    }
}
