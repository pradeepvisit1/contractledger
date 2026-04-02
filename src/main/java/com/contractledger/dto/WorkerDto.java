package com.contractledger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class WorkerDto {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String role;
        private String phone;
        private BigDecimal dailyWage;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String role;
        private String phone;
        private BigDecimal dailyWage;
        private boolean active;
        private String createdAt;
    }
}
