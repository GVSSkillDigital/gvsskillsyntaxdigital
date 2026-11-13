package com.gvdigital.training.dto;


public record RefreshResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}