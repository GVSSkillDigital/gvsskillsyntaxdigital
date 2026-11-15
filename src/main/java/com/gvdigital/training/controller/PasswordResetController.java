package com.gvdigital.training.controller;

import com.gvdigital.training.dto.ConfirmCodeRequest;
import com.gvdigital.training.dto.MessageResponse;
import com.gvdigital.training.dto.PasswordResetRequest;
import com.gvdigital.training.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(
        PasswordResetService passwordResetService
    ) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/passwordreset")
    public ResponseEntity<MessageResponse> requestPasswordReset(
        @Valid @RequestBody PasswordResetRequest request,
        HttpServletRequest httpRequest
    ) {
        passwordResetService.requestPasswordReset(
            request,
            httpRequest.getRemoteAddr()
        );

        return ResponseEntity.ok(
            new MessageResponse(
                "If an account exists for that email, a verification code " +
                "will be sent."
            )
        );
    }

    @PostMapping("/confirmcode")
    public ResponseEntity<MessageResponse> confirmCode(
        @Valid @RequestBody ConfirmCodeRequest request
    ) {
        passwordResetService.confirmCode(request);

        return ResponseEntity.ok(
            new MessageResponse("Password reset successfully. You can log in.")
        );
    }
}
