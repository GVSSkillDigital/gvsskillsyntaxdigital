package com.gvdigital.training.dto;

import java.time.LocalDate;

public record UserProfileResponse(
    Long userId,
    String email,
    String fullName,
    String phone,
    String address,
    LocalDate dateOfBirth,
    String profileImageUrl,
    String gender,
    String educationLevel,
    boolean emailVerified
) {}