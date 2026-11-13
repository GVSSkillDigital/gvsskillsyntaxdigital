package com.gvdigital.training.repository;

import com.gvdigital.training.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"roles", "profile"})
    Optional<User> findWithProfileByEmailIgnoreCase(String email);

}