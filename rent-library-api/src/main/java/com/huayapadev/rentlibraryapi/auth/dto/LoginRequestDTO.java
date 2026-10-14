package com.huayapadev.rentlibraryapi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe respetar el formato de un email")
    String email,

    @NotBlank(message = "El password es requerido")
    @Size(min = 8, message = "El password debe tener al menos 8 caracteres")
    String password
) {
}
