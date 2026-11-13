package com.gvdigital.training.service;

import com.gvdigital.training.dto.UpdateProfileRequest;
import com.gvdigital.training.dto.UserProfileResponse;

import com.gvdigital.training.model.User;
import com.gvdigital.training.model.UserProfile;

import com.gvdigital.training.repository.UserProfileRepository;
import com.gvdigital.training.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    public UserService(
        UserRepository userRepository,
        UserProfileRepository profileRepository
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Principal principal) {

        User user = findUser(principal.getName());

        UserProfile profile = user.getProfile();

        if (profile == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User profile not found"
            );
        }

        return toResponse(user, profile);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(
        Principal principal,
        UpdateProfileRequest request
    ) {

        User user = findUser(principal.getName());

        UserProfile profile = user.getProfile();

        if (profile == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User profile not found"
            );
        }

        profile.setFullName(request.fullName().trim());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setProfileImageUrl(request.profileImageUrl());
        profile.setGender(request.gender());

        profileRepository.save(profile);

        return toResponse(user, profile);
    }

    private User findUser(String email) {
        return userRepository.findWithProfileByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
            ));
    }

    private UserProfileResponse toResponse(
        User user,
        UserProfile profile
    ) {
        return new UserProfileResponse(
            user.getId(),
            user.getEmail(),
            profile.getFullName(),
            profile.getPhone(),
            profile.getAddress(),
            profile.getDateOfBirth(),
            profile.getProfileImageUrl(),
            profile.getGender(),
            user.isEmailVerified()
        );
    }
}