package com.huayapadev.rentlibraryapi.author.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequestDTO (
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre tiene un máximo de 100 caracteres.")
    String name,
    @Size(max = 500, message = "La bio tiene un máximo de 500 caracteres.")
    String bio
) {}
