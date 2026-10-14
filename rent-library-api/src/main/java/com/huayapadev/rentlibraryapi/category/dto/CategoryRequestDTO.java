package com.huayapadev.rentlibraryapi.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.hateoas.server.core.Relation;

public record CategoryRequestDTO(
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 255, message = "El nombre tiene un máximo de 255 caracteres.")
    String name
) {}
