package com.gvdigital.training.service;

import com.gvdigital.training.dto.ConfirmCodeRequest;
import com.gvdigital.training.dto.PasswordResetRequest;
import com.gvdigital.training.model.PasswordResetHistory;
import com.gvdigital.training.model.PasswordResetHistory.Status;
import com.gvdigital.training.model.User;
import com.gvdigital.training.repository.PasswordResetHistoryRepository;
import com.gvdigital.training.repository.RefreshTokenRepository;
import com.gvdigital.training.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PasswordResetService {

    private static final Logger log =
            LoggerFactory.getLogger(PasswordResetService.class);
    private static final int MAX_REQUESTS_PER_EMAIL = 3;
    private static final int MAX_REQUESTS_PER_IP = 10;
    private static final int MAX_CODE_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PasswordResetHistoryRepository resetHistoryRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public PasswordResetService(
        PasswordResetHistoryRepository resetHistoryRepository,
        UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        JavaMailSender mailSender
    ) {
        this.resetHistoryRepository = resetHistoryRepository;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public void requestPasswordReset(
        PasswordResetRequest request,
        String requestIp
    ) {
        String email = normalizeEmail(request.email());
        validatePasswords(request.password(), request.confirmPassword());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rateLimitWindow = now.minusMinutes(15);
        boolean rateLimited =
            resetHistoryRepository.countByEmailIgnoreCaseAndRequestedAtAfter(
                email,
                rateLimitWindow
            ) >= MAX_REQUESTS_PER_EMAIL
            || resetHistoryRepository.countByRequestIpAndRequestedAtAfter(
                requestIp,
                rateLimitWindow
            ) >= MAX_REQUESTS_PER_IP;

        if (rateLimited) {
            return;
        }

        String code = String.format(
            Locale.ROOT,
            "%06d",
            SECURE_RANDOM.nextInt(1_000_000)
        );

        PasswordResetHistory history = new PasswordResetHistory();
        history.setEmail(email);
        history.setVerificationCodeHash(passwordEncoder.encode(code));
        history.setPendingPasswordHash(
            passwordEncoder.encode(request.password())
        );
        history.setRequestIp(requestIp);
        history.setRequestedAt(now);
        history.setExpiresAt(now.plusMinutes(3));
        history.setStatus(Status.ACTIVE);
        history.setFailedAttempts(0);

        resetHistoryRepository
            .findAllByEmailIgnoreCaseAndStatus(email, Status.ACTIVE)
            .forEach(previous -> previous.setStatus(Status.EXPIRED));
        resetHistoryRepository.save(history);

        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            try {
                sendResetCode(user, code);
            } catch (RuntimeException exception) {
                history.setStatus(Status.EXPIRED);
                log.error("Could not send a password-reset email", exception);
            }
        });
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void confirmCode(ConfirmCodeRequest request) {
        String email = normalizeEmail(request.email());

        PasswordResetHistory history = resetHistoryRepository
            .findFirstByEmailIgnoreCaseAndStatusOrderByRequestedAtDesc(
                email,
                Status.ACTIVE
            )
            .orElseThrow(this::invalidCode);

        if (!history.getExpiresAt().isAfter(LocalDateTime.now())) {
            history.setStatus(Status.EXPIRED);
            resetHistoryRepository.save(history);
            throw invalidCode();
        }

        if (history.getFailedAttempts() >= MAX_CODE_ATTEMPTS) {
            history.setStatus(Status.EXPIRED);
            resetHistoryRepository.save(history);
            throw invalidCode();
        }

        if (!passwordEncoder.matches(
                request.code(),
                history.getVerificationCodeHash()
            )) {
            int failedAttempts = history.getFailedAttempts() + 1;
            history.setFailedAttempts(failedAttempts);

            if (failedAttempts >= MAX_CODE_ATTEMPTS) {
                history.setStatus(Status.EXPIRED);
            }

            resetHistoryRepository.save(history);
            throw invalidCode();
        }

        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> {
                history.setStatus(Status.EXPIRED);
                resetHistoryRepository.save(history);
                return invalidCode();
            });

        user.setPasswordHash(history.getPendingPasswordHash());
        userRepository.save(user);

        history.setStatus(Status.USED);
        resetHistoryRepository.save(history);
        refreshTokenRepository.deleteAllByUserId(user.getId());
    }

    private void sendResetCode(User user, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(user.getEmail());
        message.setSubject("Your GVS SkillSyntax Digital password reset code");
        message.setText(
            "Your password reset code is: " + code + "\n\n" +
            "This code expires in 3 minutes. If you did not request " +
            "a password reset, you can ignore this email."
        );

        mailSender.send(message);
    }

    private void validatePasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password and confirm password must match"
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must not exceed 72 UTF-8 bytes"
            );
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private ResponseStatusException invalidCode() {
        return new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Invalid or expired password reset code"
        );
    }
}
