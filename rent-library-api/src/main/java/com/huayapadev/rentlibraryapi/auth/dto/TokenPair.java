package com.huayapadev.rentlibraryapi.auth.dto;

public record TokenPair(String accessToken, String refreshToken, String refreshTokenId) {
}
