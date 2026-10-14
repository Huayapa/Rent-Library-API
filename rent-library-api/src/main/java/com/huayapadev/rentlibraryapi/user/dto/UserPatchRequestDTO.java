package com.huayapadev.rentlibraryapi.user.dto;

import org.openapitools.jackson.nullable.JsonNullable;

public record UserPatchRequestDTO(
    JsonNullable<String> name
) {
    public UserPatchRequestDTO {
        name = name == null ? JsonNullable.undefined() : name;
    }
}
