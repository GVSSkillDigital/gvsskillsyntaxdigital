package com.gvdigital.training.controller;

import com.gvdigital.training.dto.MessageResponse;
import com.gvdigital.training.model.User;
import com.gvdigital.training.repository.UserRepository;
import com.gvdigital.training.service.EmailVerificationService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class EmailVerificationController {

    private final EmailVerificationService verificationService;
    private final UserRepository userRepository;

    public EmailVerificationController(
        EmailVerificationService verificationService,
        UserRepository userRepository
    ) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
    }

    public record VerifyRequest(
        @NotBlank String token
    ) {}

    public record ResendRequest(
        @NotBlank @Email String email
    ) {}

    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(
        @RequestBody VerifyRequest request
    ) {
        verificationService.verifyEmail(request.token());

        return ResponseEntity.ok(
            new MessageResponse("Email verified successfully")
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(
        @RequestBody ResendRequest request
    ) {
        userRepository.findByEmailIgnoreCase(
            request.email().trim().toLowerCase()
        ).ifPresent(user -> {
            if (!user.isEmailVerified()
                && "ACTIVE".equals(user.getAccountStatus())) {
                verificationService.sendVerificationEmail(user);
            }
        });

        // Same response whether or not an account was found.
        return ResponseEntity.ok(
            new MessageResponse(
                "If an eligible account exists, a verification email will be sent."
            )
        );
    }
}