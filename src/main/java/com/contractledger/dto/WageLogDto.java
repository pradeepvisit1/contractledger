package com.contractledger.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WageLogDto {

    @Data
    public static class Request {
        @NotNull
        private LocalDate logDate;
        @NotEmpty
        private List<Long> workerIds;
        private String notes;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private LocalDate logDate;
        private BigDecimal totalAmount;
        private String notes;
        private String loggedBy;
        private String createdAt;
        private List<String> workerNames;
        private int workerCount;
    }
}
