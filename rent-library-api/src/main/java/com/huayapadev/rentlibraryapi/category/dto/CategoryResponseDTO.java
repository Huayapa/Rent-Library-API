package com.huayapadev.rentlibraryapi.category.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "categories", itemRelation = "category")
public record CategoryResponseDTO(
    Long id,
    String name
) {}
