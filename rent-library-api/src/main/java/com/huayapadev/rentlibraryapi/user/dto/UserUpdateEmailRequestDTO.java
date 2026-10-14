package com.huayapadev.rentlibraryapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateEmailRequestDTO(
    @NotBlank(message = "El nuevo correo es requerido")
    @Email(message = "El correo debe ser valido")
    String email
) {
}
