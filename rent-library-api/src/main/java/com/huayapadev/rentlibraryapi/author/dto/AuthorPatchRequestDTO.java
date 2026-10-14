package com.huayapadev.rentlibraryapi.author.dto;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record AuthorPatchRequestDTO(
    @Size(max = 100, message = "El nombre tiene un máximo de 100 caracteres.") JsonNullable<String> name,
    @Size(max = 500, message = "La bio tiene un máximo de 500 caracteres.") JsonNullable<String> bio
) {
    public AuthorPatchRequestDTO {
        name = name == null ? JsonNullable.undefined() : name;
        bio = bio == null ? JsonNullable.undefined() : bio;
    }
}
