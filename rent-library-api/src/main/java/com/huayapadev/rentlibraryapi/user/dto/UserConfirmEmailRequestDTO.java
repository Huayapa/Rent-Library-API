package com.huayapadev.rentlibraryapi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserConfirmEmailRequestDTO(
    @NotBlank(message = "El código es requerido") String code
) {
}
