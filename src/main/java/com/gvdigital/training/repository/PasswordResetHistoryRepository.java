package com.gvdigital.training.repository;

import com.gvdigital.training.model.PasswordResetHistory;
import com.gvdigital.training.model.PasswordResetHistory.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetHistoryRepository
        extends JpaRepository<PasswordResetHistory, Long> {

    long countByEmailIgnoreCaseAndRequestedAtAfter(
        String email,
        LocalDateTime requestedAfter
    );

    long countByRequestIpAndRequestedAtAfter(
        String requestIp,
        LocalDateTime requestedAfter
    );

    List<PasswordResetHistory> findAllByEmailIgnoreCaseAndStatus(
        String email,
        Status status
    );

    Optional<PasswordResetHistory>
            findFirstByEmailIgnoreCaseAndStatusOrderByRequestedAtDesc(
                String email,
                Status status
            );
}
