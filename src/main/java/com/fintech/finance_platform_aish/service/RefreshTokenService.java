package com.fintech.finance_platform_aish.service;

import com.fintech.finance_platform_aish.entity.RefreshToken;
import com.fintech.finance_platform_aish.entity.User;
import com.fintech.finance_platform_aish.repository.RefreshTokenRepository;
import com.fintech.finance_platform_aish.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;

    }

    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {

        User user = oldToken.getUser();

        refreshTokenRepository.delete(oldToken);

        return createRefreshToken(user);
    }

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        return token;
    }

    @Transactional
    public void logout(String refreshToken){

        RefreshToken token = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = token.getUser();

        refreshTokenRepository.deleteByUser(user);
    }
}