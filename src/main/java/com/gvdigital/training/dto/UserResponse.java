package com.gvdigital.training.dto;

import java.util.Set;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    boolean emailVerified,
    Set<String> roles
) {}