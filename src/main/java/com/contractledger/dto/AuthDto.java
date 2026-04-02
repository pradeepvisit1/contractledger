package com.contractledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class AuthDto {

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    @Builder
    public static class AuthResponse {
        private String token;
        private String username;
        private String fullName;
        private String role;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Size(min = 6)
        private String password;

        private String fullName;
        private String phone;
        private String role;         // "ADMIN" or "USER"
        private String adminSecret;  // required only when role = ADMIN
    }
}
