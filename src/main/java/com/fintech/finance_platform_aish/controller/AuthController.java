package com.fintech.finance_platform_aish.controller;

import com.fintech.finance_platform_aish.dto.LoginRequest;
import com.fintech.finance_platform_aish.dto.LoginResponse;
import com.fintech.finance_platform_aish.entity.RefreshToken;
import com.fintech.finance_platform_aish.entity.User;
import com.fintech.finance_platform_aish.repository.RefreshTokenRepository;
import com.fintech.finance_platform_aish.repository.UserRepository;
import com.fintech.finance_platform_aish.service.RefreshTokenService;
import com.fintech.finance_platform_aish.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil, UserRepository userRepository, RefreshTokenService refreshTokenService,RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        Map<String, String> response = new HashMap<>();

        response.put("accessToken",accessToken);
        response.put("refreshToken",refreshToken.getToken());


        return response;
    }

    @PostMapping("/refresh")
    public Map<String, String> refreshToken(@RequestBody Map<String, String> request) {

        String requestToken = request.get("refreshToken");

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(requestToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        // Generate a new accessToken
        String newAccessToken =
                jwtUtil.generateToken(user.getEmail(), user.getRole());

        //Rotate refresh token
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

        Map<String, String> response = new HashMap<>();

        response.put("accessToken", newAccessToken);
        response.put("refreshToken",newRefreshToken.getToken());

        return response;
    }

    @PostMapping("/logout")
    public String logout(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");
        refreshTokenService.logout(refreshToken);


        return "Logged out successfully";
    }
}
