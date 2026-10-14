package com.huayapadev.rentlibraryapi.auth.controller;

import com.huayapadev.rentlibraryapi.auth.dto.*;
import com.huayapadev.rentlibraryapi.auth.mapper.AuthMapper;
import com.huayapadev.rentlibraryapi.auth.service.AuthService;
import com.huayapadev.rentlibraryapi.auth.service.JwtService;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final String cookieName = "refresh_token";
    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> register(
        @Valid @RequestBody RegisterRequestDTO request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        AuthResultDTO result = authService.register(request, httpRequest);
        setRefreshToken(result, httpResponse);
        TokenResponseDTO response = authMapper.toResponseDTO(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // TODO: Endpoint temporal para crear usuario admin
    @PostMapping("/register/admin")
    public ResponseEntity<TokenResponseDTO> registerAdmin(
        @Valid @RequestBody RegisterRequestDTO request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        AuthResultDTO result = authService.registerAdmin(request, httpRequest);
        setRefreshToken(result, httpResponse);
        TokenResponseDTO response = authMapper.toResponseDTO(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
        @Valid @RequestBody LoginRequestDTO request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse,
        @CookieValue(value = cookieName, required = false) String currentRefreshToken
    ) {
        AuthResultDTO result = authService.login(request, httpRequest, currentRefreshToken);
        setRefreshToken(result, httpResponse);
        return ResponseEntity.ok(authMapper.toResponseDTO(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@CookieValue(cookieName) String currentRefreshToken) {
        AuthResultDTO result = authService.refresh(currentRefreshToken);
        return ResponseEntity.ok(authMapper.toResponseDTO(result));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionResponseDTO>> getMyActiveSessionAll(
        @AuthenticationPrincipal UserModel user,
        @CookieValue(value = cookieName) String currentRefreshToken
    ) {
        List<SessionResponseDTO> authList = authService.getActiveSessions(user, currentRefreshToken);
        return ResponseEntity.ok(authList);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
        @AuthenticationPrincipal UserModel user,
        @CookieValue(value = cookieName) String currentRefreshToken,
        HttpServletResponse httpResponse
    ) {
        authService.logout(user, currentRefreshToken);
        clearRefreshCookie(httpResponse);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> logoutSessions(
        @AuthenticationPrincipal UserModel user,
        HttpServletResponse httpResponse
    ) {
        authService.revokeAllSessions(user);
        clearRefreshCookie(httpResponse);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions/{tokenId}")
    public ResponseEntity<Void> logoutSessionByTokenId(
        @PathVariable String tokenId,
        @AuthenticationPrincipal UserModel user,
        @CookieValue(value = cookieName, required = false) String currentRefreshToken,
        HttpServletResponse httpResponse
    ) {
        authService.revokeSession(user, tokenId);

        if(StringUtils.hasText(currentRefreshToken)) {
            String currentTokenId = jwtService.extractTokenId(currentRefreshToken);
            if(tokenId.equals(currentTokenId)) {
                clearRefreshCookie(httpResponse);
            }
        }
        return ResponseEntity.noContent().build();
    }

    private void setRefreshToken(AuthResultDTO result, HttpServletResponse httpResponse) {
        Instant expiration = jwtService.extractExpiration(result.refreshToken());
        long maxAgeSeconds = Duration.between(Instant.now(), expiration).getSeconds();
        ResponseCookie cookie = ResponseCookie.from(cookieName, result.refreshToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/api/auth")
            .maxAge(maxAgeSeconds)
            .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse httpResponse) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/api/auth")
            .maxAge(0)
            .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}