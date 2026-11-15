package com.gvdigital.training.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gvdigital.training.dto.ConfirmCodeRequest;
import com.gvdigital.training.dto.PasswordResetRequest;
import com.gvdigital.training.model.PasswordResetHistory;
import com.gvdigital.training.model.PasswordResetHistory.Status;
import com.gvdigital.training.model.User;
import com.gvdigital.training.repository.PasswordResetHistoryRepository;
import com.gvdigital.training.repository.RefreshTokenRepository;
import com.gvdigital.training.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetHistoryRepository resetHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setSenderEmail() {
        ReflectionTestUtils.setField(
            passwordResetService,
            "senderEmail",
            "noreply@example.com"
        );
    }

    @Test
    void requestPasswordResetSendsCodeAndStoresHashedValues() {
        User user = new User();
        user.setEmail("student@example.com");

        when(resetHistoryRepository
                .countByEmailIgnoreCaseAndRequestedAtAfter(
                    anyString(),
                    any(LocalDateTime.class)
                ))
            .thenReturn(0L);
        when(resetHistoryRepository
                .countByRequestIpAndRequestedAtAfter(
                    anyString(),
                    any(LocalDateTime.class)
                ))
            .thenReturn(0L);
        when(resetHistoryRepository
                .findAllByEmailIgnoreCaseAndStatus("student@example.com", Status.ACTIVE))
            .thenReturn(List.of());
        when(passwordEncoder.encode(anyString()))
            .thenAnswer(invocation -> "encoded:" + invocation.getArgument(0));
        when(userRepository.findByEmailIgnoreCase("student@example.com"))
            .thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset(
            new PasswordResetRequest(
                " Student@Example.com ",
                "new-password",
                "new-password"
            ),
            "127.0.0.1"
        );

        ArgumentCaptor<PasswordResetHistory> historyCaptor =
            ArgumentCaptor.forClass(PasswordResetHistory.class);
        verify(resetHistoryRepository).save(historyCaptor.capture());
        PasswordResetHistory history = historyCaptor.getValue();

        assertEquals("student@example.com", history.getEmail());
        assertEquals(Status.ACTIVE, history.getStatus());
        assertEquals("127.0.0.1", history.getRequestIp());
        assertEquals("encoded:new-password", history.getPendingPasswordHash());
        assertEquals(0, history.getFailedAttempts());
        assertEquals(
            history.getRequestedAt().plusMinutes(3),
            history.getExpiresAt()
        );
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void confirmCodeUpdatesPasswordAndConsumesCode() {
        PasswordResetHistory history = new PasswordResetHistory();
        history.setEmail("student@example.com");
        history.setVerificationCodeHash("encoded:123456");
        history.setPendingPasswordHash("encoded:new-password");
        history.setExpiresAt(LocalDateTime.now().plusMinutes(2));
        history.setStatus(Status.ACTIVE);

        User user = new User();
        user.setEmail("student@example.com");
        ReflectionTestUtils.setField(user, "id", 42L);

        when(resetHistoryRepository
                .findFirstByEmailIgnoreCaseAndStatusOrderByRequestedAtDesc(
                    "student@example.com",
                    Status.ACTIVE
                ))
            .thenReturn(Optional.of(history));
        when(passwordEncoder.matches("123456", "encoded:123456"))
            .thenReturn(true);
        when(userRepository.findByEmailIgnoreCase("student@example.com"))
            .thenReturn(Optional.of(user));

        passwordResetService.confirmCode(
            new ConfirmCodeRequest("student@example.com", "123456")
        );

        assertEquals("encoded:new-password", user.getPasswordHash());
        assertEquals(Status.USED, history.getStatus());
        verify(userRepository).save(user);
        verify(resetHistoryRepository).save(history);
        verify(refreshTokenRepository).deleteAllByUserId(42L);
    }

    @Test
    void fiveIncorrectCodesExpireTheResetRequest() {
        PasswordResetHistory history = new PasswordResetHistory();
        history.setEmail("student@example.com");
        history.setVerificationCodeHash("encoded:correct");
        history.setExpiresAt(LocalDateTime.now().plusMinutes(2));
        history.setStatus(Status.ACTIVE);

        when(resetHistoryRepository
                .findFirstByEmailIgnoreCaseAndStatusOrderByRequestedAtDesc(
                    "student@example.com",
                    Status.ACTIVE
                ))
            .thenReturn(Optional.of(history));
        when(passwordEncoder.matches("000000", "encoded:correct"))
            .thenReturn(false);

        ConfirmCodeRequest request =
            new ConfirmCodeRequest("student@example.com", "000000");

        for (int attempt = 0; attempt < 5; attempt++) {
            assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.confirmCode(request)
            );
        }

        assertEquals(5, history.getFailedAttempts());
        assertEquals(Status.EXPIRED, history.getStatus());
        verify(resetHistoryRepository, times(5)).save(history);
    }
}
