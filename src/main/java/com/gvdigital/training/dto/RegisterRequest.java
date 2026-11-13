package com.gvdigital.training.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank
    @Size(max = 150)
    String fullName,

    @NotBlank
    @Email
    @Size(max = 254)
    String email,

    @NotBlank
    @Size(min = 10, max = 20)
    String phone,

    @NotBlank
    @Size(min = 8, max = 72)
    String password,

    @NotBlank
    String courseCode,

    @NotBlank
    String educationLevel

) {}