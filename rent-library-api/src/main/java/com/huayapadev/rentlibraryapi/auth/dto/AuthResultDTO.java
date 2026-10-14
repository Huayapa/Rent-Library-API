package com.huayapadev.rentlibraryapi.auth.dto;

public record AuthResultDTO(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn
) {
}
