package com.huayapadev.rentlibraryapi.auth.dto;

import java.time.Instant;

public record SessionResponseDTO(
    String tokenId,
    String deviceLabel,
    Instant issuedAt,
    boolean isCurrentSession
) {
}
