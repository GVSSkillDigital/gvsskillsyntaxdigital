
package com.gvdigital.training.service;

import com.gvdigital.training.dto.*;
import com.gvdigital.training.model.*;
import com.gvdigital.training.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository profileRepository;
    private final CourseRepository courseRepository;
    private final StudentRegistrationRepository
            studentRegistrationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository
            emailVerificationTokenRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.refresh.expiration-days:7}")
    private long refreshExpirationDays;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserProfileRepository profileRepository,
            CourseRepository courseRepository,
            StudentRegistrationRepository studentRegistrationRepository,
            RefreshTokenRepository refreshTokenRepository,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailVerificationService emailVerificationService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.profileRepository = profileRepository;
        this.courseRepository = courseRepository;
        this.studentRegistrationRepository =
                studentRegistrationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationTokenRepository =
                emailVerificationTokenRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailVerificationService = emailVerificationService;
    }

    /**
     * Registers a new student account.
     *
     * The account is ACTIVE immediately.
     * Email verification is not required for login.
     *
     * This method creates the User, UserProfile, and
     * StudentRegistration records in one transaction.
     */
    @Transactional
    public void registerStudent(RegisterRequest request) {

        String email = normalizeEmail(request.email());
        String courseCode = request.courseCode().trim();

        validatePassword(request.password());

        if (!courseRepository.existsByCode(courseCode)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The selected course does not exist."
            );
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account already exists with this email. " +
                    "Please log in."
            );
        }

        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_STUDENT is missing from the database"
                ));

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setAccountStatus("ACTIVE");
        user.setEmailVerified(false);
        user.getRoles().add(studentRole);

        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName(request.fullName().trim());
        profile.setPhone(request.phone().trim());
        profile.setEducationLevel(request.educationLevel().trim());

        profileRepository.save(profile);

        StudentRegistration registration = new StudentRegistration();
        registration.setFullName(profile.getFullName());
        registration.setEmail(email);
        registration.setPhone(profile.getPhone());
        registration.setCourseCode(courseCode);

        studentRegistrationRepository.save(registration);
    }

    /**
     * Sends the verification email after registerStudent()
     * has completed and its database transaction has committed.
     *
     * This is deliberately a separate method so an email
     * provider failure cannot roll back account creation.
     */
    public void sendVerificationEmailForNewAccount(String email) {

        String normalizedEmail = normalizeEmail(email);

        try {
            User user = userRepository
                    .findByEmailIgnoreCase(normalizedEmail)
                    .orElseThrow(() -> new IllegalStateException(
                            "Newly registered user was not found"
                    ));

            if (!user.isEmailVerified()
                    && "ACTIVE".equals(user.getAccountStatus())) {

                emailVerificationService.sendVerificationEmail(user);
            }

        } catch (RuntimeException exception) {
            log.warn(
                    "Could not send verification email for a newly " +
                    "registered account. The account remains active."
            );

            // The student can request another verification email later.
        }
    }

    /**
     * Authenticates the student using email and password.
     *
     * Email verification is intentionally not checked here.
     */
    @Transactional
    public AuthSessionResult login(LoginRequest request) {

        String email = normalizeEmail(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.password()
                    )
            );

        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        User user = userRepository
                .findWithProfileByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (!"ACTIVE".equals(user.getAccountStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is not active"
            );
        }

        String accessToken = jwtService.generateAccessToken(user);

        String rawRefreshToken = createRefreshToken(user);

        UserResponse userResponse = toUserResponse(user);

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationMillis() / 1000,
                userResponse
        );

        return new AuthSessionResult(
                authResponse,
                rawRefreshToken
        );
    }

    /**
     * Validates and rotates a refresh token.
     *
     * The old refresh token is revoked.
     * A new refresh token and access token are issued.
     */
    @Transactional
    public AuthSessionResult refreshSession(String rawRefreshToken) {

        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token is required"
            );
        }

        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid refresh session"
                ));

        if (!storedToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh session has expired. Please log in again."
            );
        }

        User user = storedToken.getUser();

        if (!"ACTIVE".equals(user.getAccountStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is not active"
            );
        }

        // Revoke the old refresh token.
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // Create a replacement refresh token.
        String newRawRefreshToken = createRefreshToken(user);

        // Create a replacement access token.
        String newAccessToken = jwtService.generateAccessToken(user);

        UserResponse userResponse = toUserResponse(user);

        AuthResponse authResponse = new AuthResponse(
                newAccessToken,
                "Bearer",
                jwtService.getExpirationMillis() / 1000,
                userResponse
        );

        return new AuthSessionResult(
                authResponse,
                newRawRefreshToken
        );
    }

    /**
     * Revokes the supplied refresh token.
     *
     * Calling logout without a refresh token is harmless.
     */
    @Transactional
    public void logout(String rawRefreshToken) {

        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }

        String tokenHash = hashToken(rawRefreshToken);

        refreshTokenRepository
                .findByTokenHashAndRevokedFalse(tokenHash)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    /**
     * Creates a cryptographically random refresh token.
     *
     * Only its SHA-256 hash is stored in MySQL.
     * The raw value is returned to the caller for the cookie.
     */
    private String createRefreshToken(User user) {

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusDays(refreshExpirationDays)
        );
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    /**
     * Builds the safe user response.
     *
     * Password hashes and token values are never returned.
     */
    private UserResponse toUserResponse(User user) {

        UserProfile profile = user.getProfile();

        String fullName = profile != null
                ? profile.getFullName()
                : null;
        String phone = profile != null
                ? profile.getPhone()
                : null;
        String address = profile != null
                ? profile.getAddress()
                : null;
        LocalDate dateOfBirth = profile != null
                ? profile.getDateOfBirth()
                : null;
        String profileImageUrl = profile != null
                ? profile.getProfileImageUrl()
                : null;
        String gender = profile != null
                ? profile.getGender()
                : null;
        String educationLevel = profile != null
                ? profile.getEducationLevel()
                : null;

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                fullName,
                phone,
                address,
                dateOfBirth,
                profileImageUrl,
                gender,
                educationLevel,
                user.isEmailVerified(),
                roles
        );
    }

    private String normalizeEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * BCrypt has a 72-byte input limit.
     * Check UTF-8 byte length rather than Java character count.
     */
    private void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required"
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must not exceed 72 UTF-8 bytes"
            );
        }
    }

    /**
     * SHA-256 hash for opaque refresh tokens.
     */
    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to hash token",
                    exception
            );
        }
    }
}
