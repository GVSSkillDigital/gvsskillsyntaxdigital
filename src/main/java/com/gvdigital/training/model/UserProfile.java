package com.gvdigital.training.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;
    private String address;

    // Date of Birth
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Default Profile Image
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl =
            "https://cdn-icons-png.flaticon.com/512/3135/3135715.png";

    // Gender
    @Column(length = 20)
    private String gender;

     // Education Level
    @Column(name = "education_level", length = 100)
    private String educationLevel;

}