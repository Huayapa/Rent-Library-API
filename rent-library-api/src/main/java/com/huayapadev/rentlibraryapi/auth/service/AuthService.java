package com.huayapadev.rentlibraryapi.auth.service;

import com.huayapadev.rentlibraryapi.auth.component.Sha256TokenHasher;
import com.huayapadev.rentlibraryapi.auth.dto.*;
import com.huayapadev.rentlibraryapi.auth.mapper.SessionMapper;
import com.huayapadev.rentlibraryapi.auth.model.TokenModel;
import com.huayapadev.rentlibraryapi.auth.model.enums.TokenTypeEnum;
import com.huayapadev.rentlibraryapi.auth.repository.TokenRepository;
import com.huayapadev.rentlibraryapi.shared.exception.AlreadyExistsException;
import com.huayapadev.rentlibraryapi.shared.exception.InvalidRefreshTokenException;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import com.huayapadev.rentlibraryapi.user.UserRepository;
import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final SessionMapper sessionMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final Sha256TokenHasher sha256TokenHasher;

    @Transactional
    public AuthResultDTO register(RegisterRequestDTO request, HttpServletRequest httpRequest) {
        boolean exists = userRepository.existsByDniIgnoringSoftDelete(request.dni()) ||
            userRepository.existsByEmailIgnoringSoftDelete(request.email());
        if(exists) {
            throw new AlreadyExistsException("No se pudo completar el registro con los datos proporcionados.");
        }
        UserModel savedUser = registerUser(request, UserRoleEnum.MEMBER);
        return issueTokens(savedUser, httpRequest);
    }

    @Transactional
    public AuthResultDTO registerAdmin(RegisterRequestDTO request, HttpServletRequest httpRequest) {
        boolean exists = userRepository.existsByDniIgnoringSoftDelete(request.dni()) ||
            userRepository.existsByEmailIgnoringSoftDelete(request.email());
        if(exists) {
            throw new AlreadyExistsException("No se pudo completar el registro con los datos proporcionados.");
        }
        UserModel savedUser = registerUser(request, UserRoleEnum.ADMIN);
        return issueTokens(savedUser, httpRequest);
    }

    private UserModel registerUser(RegisterRequestDTO requestUser, UserRoleEnum role) {
        UserModel user = new UserModel();
        user.setDni(requestUser.dni());
        user.setName(requestUser.name());
        user.setEmail(requestUser.email());
        user.setRole(role);
        String passwordHash = passwordEncoder.encode(requestUser.password());
        user.setPasswordHash(passwordHash);
        return userRepository.save(user);
    }

    @Transactional
    public AuthResultDTO login(LoginRequestDTO request, HttpServletRequest httpRequest, String currentRefreshToken) {
        UserModel user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (StringUtils.hasText(currentRefreshToken)) {
            try {
                String tokenId = jwtService.extractTokenId(currentRefreshToken);
                tokenRepository.findByTokenId(tokenId)
                    .filter(t -> t.getUser().getId().equals(user.getId()))
                    .ifPresent(t -> tokenRepository.revokeByTokenId(tokenId));
            } catch (JwtException e) {
                // Ignorando error porque la cookie que tiene no funciona y no debe afectar la solicitud
            }
        }

        return issueTokens(user, httpRequest);
    }

    @Transactional
    public AuthResultDTO refresh(String rawRefreshToken) {
        String refreshTokenId = getIdByRefreshToken(rawRefreshToken);
        TokenModel token = tokenRepository.findByTokenId(refreshTokenId)
            .orElseThrow(() -> new BadCredentialsException("Sesión inválida"));
        if(token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
            throw new CredentialsExpiredException("Sesión expirada o revocada");
        }

        UserModel user = token.getUser();
        String jwtToken = jwtService.generateAccessToken(user, refreshTokenId);
        return toResponseDTO(jwtToken, rawRefreshToken);
    }

    @Transactional
    public List<SessionResponseDTO> getActiveSessions(UserModel user, String currentRefreshToken) {
        String currentTokenId = getIdByRefreshToken(currentRefreshToken);
        return tokenRepository.findByUserAndIsRevokedFalseAndExpiryDateAfter(user, Instant.now())
            .stream()
            .map(token -> sessionMapper.toModel(token, currentTokenId))
            .toList();
    }

    @Transactional
    public void logout(UserModel user, String currentRefreshToken) {
        String tokenId = getIdByRefreshToken(currentRefreshToken);
        int updated = tokenRepository.revokeByTokenIdAndUserId(tokenId, user.getId());
        if (updated == 0) {
            throw new EntityNotFoundException("Sesión no encontrada");
        }
    }

    @Transactional
    public void revokeAllSessions(UserModel user) {
        tokenRepository.revokeAllSessionsByUserId(user.getId());
    }

    @Transactional
    public void validatePassword(UserModel user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña invalida");
        }
    }

    @Transactional
    public void revokeSession(UserModel user, String tokenId) {
        int updated = tokenRepository.revokeByTokenIdAndUserId(tokenId, user.getId());
        if (updated == 0) {
            throw new EntityNotFoundException("Sesión no encontrada");
        }
    }

    public boolean isRevokedByTokenId(String tokenId) {
        return tokenRepository.existsByTokenIdAndIsRevokedTrue(tokenId);
    }

    private String getIdByRefreshToken(String rawRefreshToken) {
        if(!StringUtils.hasText(rawRefreshToken)) {
            throw new InvalidRefreshTokenException("Refresh token no encontrada");
        }
        try {
            return jwtService.extractTokenId(rawRefreshToken);
        } catch (JwtException e) {
            throw new InvalidRefreshTokenException("Refresh token inválido");
        }
    }

    private AuthResultDTO issueTokens(UserModel user, HttpServletRequest httpRequest) {
        TokenPair jwtToken = jwtService.generateTokenPair(user);
        saveUserToken(user, jwtToken.refreshToken(), httpRequest);
        return toResponseDTO(jwtToken.accessToken(), jwtToken.refreshToken());
    }

    private AuthResultDTO toResponseDTO(String accessToken, String refreshToken) {
        return new AuthResultDTO(
            accessToken,
            refreshToken,
            TokenTypeEnum.BEARER.getValue(),
            jwtService.extractExpiresInSeconds(accessToken)
        );
    }

    private void saveUserToken(UserModel user, String jwtToken, HttpServletRequest request) {
        TokenModel token = TokenModel.builder()
            .user(user)
            .tokenId(jwtService.extractTokenId(jwtToken))
            .token(sha256TokenHasher.hashToken(jwtToken))
            .expiryDate(jwtService.extractExpiration(jwtToken))
            .issuedAt(Instant.now())
            .isRevoked(false)
            .deviceInfo(request.getHeader("User-Agent"))
            .build();
        tokenRepository.save(token);
    }
}
