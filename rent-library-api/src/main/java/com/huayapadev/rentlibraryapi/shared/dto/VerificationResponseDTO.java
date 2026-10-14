package com.huayapadev.rentlibraryapi.shared.dto;

import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;

@Relation(collectionRelation = "verifications", itemRelation = "verification")
public record VerificationResponseDTO(
    String message,
    Instant expiredAt
) {
}
