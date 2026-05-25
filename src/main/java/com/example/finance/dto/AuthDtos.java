package com.example.finance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(
            @Email @NotBlank String username,
            @NotBlank @Size(min = 8) String password,
            @NotBlank String fullName,
            @NotBlank @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "must be a valid phone number") String phoneNumber) {
    }

    public record LoginRequest(@Email @NotBlank String username, @NotBlank String password) {
    }

    public record RegisterResponse(String message, Long userId) {
    }

    public record MessageResponse(String message) {
    }
}
