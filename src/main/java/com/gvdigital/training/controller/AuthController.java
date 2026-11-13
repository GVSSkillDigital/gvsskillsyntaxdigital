package com.gvdigital.training.controller;

import com.gvdigital.training.dto.*;
import com.gvdigital.training.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "gvs_refresh";

    private final AuthService authService;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @Value("${app.refresh.expiration-days:7}")
    private long refreshExpirationDays;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Student registration.
     *
     * Creates an ACTIVE student account.
     * Email verification is not required for initial login.
     */
@PostMapping("/register")
public ResponseEntity<MessageResponse> register(
        @Valid @RequestBody RegisterRequest request) {

    authService.registerStudent(request);

    // This executes after the registration transaction commits.
    authService.sendVerificationEmailForNewAccount(request.email());

    return ResponseEntity.status(201).body(
            new MessageResponse(
                    "Registration successful. You can now log in. " +
                    "Please check your email for verification."
            )
    );
}

    /**
     * Login using email and password.
     *
     * Access token is returned in the response body.
     * Refresh token is stored in an HttpOnly cookie.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthSessionResult result = authService.login(request);

        setRefreshCookie(response, result.refreshToken());

        return ResponseEntity.ok(result.authResponse());
    }

    /**
     * Refresh an expired or soon-to-expire access token.
     *
     * The existing refresh token is revoked and replaced.
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @CookieValue(
                    name = REFRESH_COOKIE_NAME,
                    required = false
            ) String refreshToken,
            HttpServletResponse response) {

        AuthSessionResult result =
                authService.refreshSession(refreshToken);

        setRefreshCookie(response, result.refreshToken());

        AuthResponse authResponse = result.authResponse();

        return ResponseEntity.ok(
                new RefreshResponse(
                        authResponse.accessToken(),
                        authResponse.tokenType(),
                        authResponse.expiresIn()
                )
        );
    }

    /**
     * Logout.
     *
     * Revokes the refresh token in the database
     * and clears the browser cookie.
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @CookieValue(
                    name = REFRESH_COOKIE_NAME,
                    required = false
            ) String refreshToken,
            HttpServletResponse response) {

        authService.logout(refreshToken);

        clearRefreshCookie(response);

        return ResponseEntity.ok(
                new MessageResponse("Logged out successfully")
        );
    }

    private void setRefreshCookie(
            HttpServletResponse response,
            String refreshToken) {

        ResponseCookie cookie = ResponseCookie
                .from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofDays(refreshExpirationDays))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    private void clearRefreshCookie(
            HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie
                .from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }
}
