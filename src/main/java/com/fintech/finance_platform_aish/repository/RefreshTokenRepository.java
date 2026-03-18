package com.fintech.finance_platform_aish.repository;

import com.fintech.finance_platform_aish.entity.RefreshToken;
import com.fintech.finance_platform_aish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
