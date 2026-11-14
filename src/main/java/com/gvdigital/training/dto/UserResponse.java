package com.gvdigital.training.dto;

import java.time.LocalDate;
import java.util.Set;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    String phone,
    String address,
    LocalDate dateOfBirth,
    String profileImageUrl,
    String gender,
    String educationLevel,
    boolean emailVerified,
    Set<String> roles
) {}