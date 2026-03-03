package com.revshop.repository;

import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
    boolean existsByEmailIgnoreCase(String email);
}
