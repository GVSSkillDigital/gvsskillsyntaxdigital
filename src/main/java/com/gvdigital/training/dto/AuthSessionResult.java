package com.gvdigital.training.dto;

public record AuthSessionResult(
        AuthResponse authResponse,
        String refreshToken
) {
}