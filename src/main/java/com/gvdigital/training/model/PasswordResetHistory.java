package com.gvdigital.training.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "password_reset_history",
    indexes = {
        @Index(
            name = "idx_password_reset_email_requested",
            columnList = "email, requested_at"
        ),
        @Index(
            name = "idx_password_reset_ip_requested",
            columnList = "request_ip, requested_at"
        ),
        @Index(
            name = "idx_password_reset_email_status",
            columnList = "email, status"
        )
    }
)
public class PasswordResetHistory {

    public enum Status {
        ACTIVE,
        EXPIRED,
        USED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "verification_code_hash", length = 60)
    private String verificationCodeHash;

    @Column(name = "pending_password_hash", length = 60, nullable = false)
    private String pendingPasswordHash;

    @Column(name = "request_ip", length = 45, nullable = false)
    private String requestIp;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCodeHash() {
        return verificationCodeHash;
    }

    public void setVerificationCodeHash(String verificationCodeHash) {
        this.verificationCodeHash = verificationCodeHash;
    }

    public String getPendingPasswordHash() {
        return pendingPasswordHash;
    }

    public void setPendingPasswordHash(String pendingPasswordHash) {
        this.pendingPasswordHash = pendingPasswordHash;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
}
