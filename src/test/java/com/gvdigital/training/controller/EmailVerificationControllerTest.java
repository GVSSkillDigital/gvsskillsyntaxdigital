package com.gvdigital.training.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gvdigital.training.repository.UserRepository;
import com.gvdigital.training.service.EmailVerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmailVerificationControllerTest {

    @Mock
    private EmailVerificationService verificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailVerificationController controller;

    @Test
    void verifyEmailIdReturnsTrueWhenEmailExists() {
        when(userRepository.existsByEmailIgnoreCase("student@example.com"))
            .thenReturn(true);

        ResponseEntity<Boolean> response =
            controller.verifyEmailId(" student@example.com ");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
        verify(userRepository).existsByEmailIgnoreCase("student@example.com");
    }

    @Test
    void verifyEmailIdReturnsFalseWhenEmailDoesNotExist() {
        when(userRepository.existsByEmailIgnoreCase("missing@example.com"))
            .thenReturn(false);

        ResponseEntity<Boolean> response =
            controller.verifyEmailId("missing@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }

    @Test
    void verifyEmailIdReturnsFalseForBlankEmail() {
        ResponseEntity<Boolean> response = controller.verifyEmailId("  ");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }
}
