package com.huayapadev.rentlibraryapi.book.dto;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record BookPatchRequestDTO(
    @Size(min = 13, max = 13, message = "El ISBN debe tener 13 caracteres")
    JsonNullable<String> isbn,
    JsonNullable<String> title,
    JsonNullable<String> description,
    JsonNullable<LocalDate> releaseDate,
    JsonNullable<Boolean> isPublished,
    JsonNullable<Long> authorId,
    JsonNullable<Long> publisherId,
    JsonNullable<Instant> scheduledPublishAt,
    JsonNullable<List<Long>> categoryIds
) {
    public BookPatchRequestDTO {
        isbn = isbn == null ? JsonNullable.undefined() : isbn;
        title = title == null ? JsonNullable.undefined() : title;
        description = description == null ? JsonNullable.undefined() : description;
        releaseDate = releaseDate == null ? JsonNullable.undefined() : releaseDate;
        isPublished = isPublished == null ? JsonNullable.undefined() : isPublished;
        authorId = authorId == null ? JsonNullable.undefined() : authorId;
        publisherId = publisherId == null ? JsonNullable.undefined() : publisherId;
        scheduledPublishAt = scheduledPublishAt == null ? JsonNullable.undefined() : scheduledPublishAt;
        categoryIds = categoryIds == null ? JsonNullable.undefined() : categoryIds;
    }
}
