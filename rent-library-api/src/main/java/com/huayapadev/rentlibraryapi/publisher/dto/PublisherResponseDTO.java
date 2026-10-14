package com.huayapadev.rentlibraryapi.publisher.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "publishers", itemRelation = "publisher")
public record PublisherResponseDTO(
    Long id,
    String name
) {
}
