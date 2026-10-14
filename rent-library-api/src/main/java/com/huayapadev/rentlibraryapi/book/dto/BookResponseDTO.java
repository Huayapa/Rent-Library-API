package com.huayapadev.rentlibraryapi.book.dto;

import com.huayapadev.rentlibraryapi.author.dto.AuthorResponseDTO;
import com.huayapadev.rentlibraryapi.category.dto.CategoryResponseDTO;
import com.huayapadev.rentlibraryapi.publisher.dto.PublisherResponseDTO;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Relation(collectionRelation = "books", itemRelation = "book")
public record BookResponseDTO(
    Long id,
    String isbn,
    String title,
    String description,
    LocalDate releaseDate,
    Boolean isPublished,
    AuthorResponseDTO author,
    PublisherResponseDTO publisher,
    Instant scheduledPublishAt,
    Instant createdAt,
    Instant updatedAt,
    List<CategoryResponseDTO> categories
) { }
