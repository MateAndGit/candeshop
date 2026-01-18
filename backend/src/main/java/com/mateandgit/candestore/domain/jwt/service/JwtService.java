package com.mateandgit.candestore.domain.jwt.service;

import com.mateandgit.candestore.domain.jwt.dto.TokenResponse;
import com.mateandgit.candestore.domain.jwt.entity.RefreshEntity;
import com.mateandgit.candestore.domain.jwt.repository.RefreshRepository;
import com.mateandgit.candestore.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    // Access token lives for 10 minutes
    private final long accessExpiration = 10 * 60 * 1000L;
//    private final long accessExpiration = 30 * 1000L;
    // Refresh token lives for 24 hours
    private final long refreshExpiration = 24 * 60 * 60 * 1000L;
//    private final long refreshExpiration = 2 * 60 * 1000L;

    public JwtService(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public TokenResponse generateTokens(String username, String role) {
        String accessToken = jwtUtil.createJwt("access", username, role, accessExpiration);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, refreshExpiration);

        // Save refresh token to DB
        addRefreshEntity(username, refreshToken, refreshExpiration);

        return new TokenResponse(accessToken, refreshToken);
    }
    
    @Transactional
    public TokenResponse reissueToken(String refreshToken) {
        // Validate refresh token
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!"refresh".equals(category)) {
            throw new IllegalArgumentException("Invalid token type");
        }

        // Check if refresh token exists in DB
        if (!refreshRepository.existsByRefresh(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Delete old refresh token from DB
        refreshRepository.deleteByRefresh(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // Generate new tokens
        return generateTokens(username, role);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity(username, refresh, date.toString());
        refreshRepository.save(refreshEntity);
    }

    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true); // For HTTPS
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshRepository.deleteByRefresh(refreshToken);
    }
}
