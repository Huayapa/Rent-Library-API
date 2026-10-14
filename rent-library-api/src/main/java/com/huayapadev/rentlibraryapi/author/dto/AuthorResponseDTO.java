package com.huayapadev.rentlibraryapi.author.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "authors", itemRelation = "author")
public record AuthorResponseDTO(
    Long id,
    String name,
    String bio
) {}
