package com.huayapadev.rentlibraryapi.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record BookRequestDTO(
    @NotBlank(message = "El ISBN es requerido")
    @Size(min = 13, max = 13, message = "El ISBN debe tener 13 caracteres")
    String isbn,
    @NotBlank(message = "El title es requerido")
    String title,
    String description,
    LocalDate releaseDate,
    Boolean isPublished,
    @NotNull(message = "El author_id es requerido")
    Long authorId,
    @NotNull(message = "El publisher_id es requerido")
    Long publisherId,
    Instant scheduledPublishAt,
    List<Long> categoryIds
) {
}
