package com.huayapadev.rentlibraryapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeleteMyAccountRequestDTO(
    @NotBlank(message = "El password es requerido")
    @Size(min = 8, max = 25, message = "El password debe tener minimo 8 y un máximo de 25 caracteres")
    String password
) {
}
