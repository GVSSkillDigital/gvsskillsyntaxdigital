package com.gvdigital.training.service;

import com.gvdigital.training.model.EmailVerificationToken;
import com.gvdigital.training.model.User;

import com.gvdigital.training.repository.EmailVerificationTokenRepository;
import com.gvdigital.training.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final SecureRandom secureRandom = new SecureRandom();

    public EmailVerificationService(
        EmailVerificationTokenRepository tokenRepository,
        UserRepository userRepository,
        JavaMailSender mailSender
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void sendVerificationEmail(User user) {

        if (user.isEmailVerified()) {
            return;
        }

        // Invalidate older unused tokens for this account.
        tokenRepository.findAllByUserIdAndUsedFalse(user.getId())
            .forEach(token -> token.setUsed(true));

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(randomBytes);

        EmailVerificationToken verificationToken =
            new EmailVerificationToken();

        verificationToken.setUser(user);
        verificationToken.setTokenHash(hashToken(rawToken));
        verificationToken.setExpiresAt(
            LocalDateTime.now().plusHours(24)
        );

        tokenRepository.save(verificationToken);

        String verificationUrl =
            frontendUrl + "/verify-email?token=" + rawToken;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(user.getEmail());
        message.setSubject("Verify your GVS SkillSyntax Digital email");

        message.setText(
            "Welcome to GVS SkillSyntax Digital!\n\n" +
            "Your student account is active. You can log in now.\n\n" +
            "Please verify your email using this link:\n" +
            verificationUrl + "\n\n" +
            "This link expires in 24 hours."
        );

        mailSender.send(message);
    }

    @Transactional
    public void verifyEmail(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Verification token is required"
            );
        }

        String hashedToken = hashToken(rawToken);

        EmailVerificationToken token =
            tokenRepository.findByTokenHashAndUsedFalse(hashedToken)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid or already used verification link"
                ));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            token.setUsed(true);

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Verification link has expired"
            );
        }

        User user = token.getUser();
        user.setEmailVerified(true);

        token.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(token);
    }

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
                "Unable to hash verification token",
                exception
            );
        }
    }
}