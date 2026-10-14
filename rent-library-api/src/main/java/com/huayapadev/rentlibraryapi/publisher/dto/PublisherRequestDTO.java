package com.huayapadev.rentlibraryapi.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublisherRequestDTO(
    @NotBlank(message = "El name es requerido")
    @Size(max = 255, message = "El name no debe exceder los 255 caracteres")
    String name
) {
}
