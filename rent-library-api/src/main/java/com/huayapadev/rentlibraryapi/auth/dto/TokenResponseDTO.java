package com.huayapadev.rentlibraryapi.auth.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tokens", itemRelation = "token")
public record TokenResponseDTO(
    String accessToken,
    String tokenType,
    long expiresIn
) {
}
