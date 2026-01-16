package com.mateandgit.candestore.handler;

import com.mateandgit.candestore.domain.jwt.dto.TokenResponse;
import com.mateandgit.candestore.domain.jwt.service.JwtService;
import com.mateandgit.candestore.domain.user.dto.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        TokenResponse tokens = jwtService.generateTokens(username, role);

        // Add refresh token to cookie
        jwtService.addRefreshTokenToCookie(response, tokens.getRefreshToken());

        // Add access token to response body
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"accessToken\": \"" + tokens.getAccessToken() + "\"}");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
