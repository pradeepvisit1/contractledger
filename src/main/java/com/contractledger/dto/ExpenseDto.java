package com.contractledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDto {

    @Data
    public static class Request {
        @NotBlank
        private String description;
        @NotNull
        @Positive
        private BigDecimal amount;
        @NotBlank
        private String category;
        private LocalDate expenseDate;
        private String paidTo;
        private String billNumber;
        private String notes;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String description;
        private BigDecimal amount;
        private String category;
        private LocalDate expenseDate;
        private String paidTo;
        private String billNumber;
        private String notes;
        private String createdBy;
        private String createdAt;
    }
}
