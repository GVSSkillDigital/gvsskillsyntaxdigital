package com.gvdigital.training.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateProfileRequest(

    @NotBlank
    @Size(max = 150)
    String fullName,

    @Size(max = 20)
    String phone,

    @Size(max = 500)
    String address,

    LocalDate dateOfBirth,

    @Size(max = 500)
    String profileImageUrl,

    @Size(max = 30)
    String gender

) {}