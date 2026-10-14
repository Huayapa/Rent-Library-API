package com.huayapadev.rentlibraryapi.copy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CopyRequestDTO(
    // NOTE: Aquí seria mejor indicar solo la ubicación de la estantería y el sistema se encargue de generar el inventoryCode
    //  Por ahora esta asi: COP-{zona}{estantería}-{numero_copia}
    //  Si es automático el numero de copia debe incrementar por estantería (con esto debes agregar una tabla de zona y estantería)
    @NotBlank(message = "El inventoryCode es requerido")
    @Size(max = 50, message = "El inventoryCode no debe exceder los 50 caracteres.")
    String inventoryCode,
    @NotNull(message = "El bookId es requerido")
    Long bookId
) {
}
