package com.gvdigital.training.controller;

import com.gvdigital.training.dto.UpdateProfileRequest;
import com.gvdigital.training.dto.UserProfileResponse;
import com.gvdigital.training.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
        Principal principal
    ) {
        return ResponseEntity.ok(
            userService.getMyProfile(principal)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
        Principal principal,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(
            userService.updateMyProfile(principal, request)
        );
    }
}