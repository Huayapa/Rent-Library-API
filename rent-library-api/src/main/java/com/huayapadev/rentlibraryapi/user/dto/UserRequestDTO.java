package com.huayapadev.rentlibraryapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank(message = "El dni es requerido")
    @Size(min = 8, max = 8, message = "El dni debe ser de 8 caracteres")
    @Pattern(regexp = "^\\d+$", message = "El dni solo acepta números")
    String dni,
    @NotBlank(message = "El name es requerido")
    String name,
    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe respetar el formato de un email")
    String email,
    @NotBlank(message = "El password es requerido")
    @Size(min = 8, max = 25, message = "El password debe tener minimo 8 y un máximo de 25 caracteres")
    String password
) {
}
