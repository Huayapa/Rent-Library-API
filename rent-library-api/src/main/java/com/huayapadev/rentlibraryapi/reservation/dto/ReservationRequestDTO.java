package com.huayapadev.rentlibraryapi.reservation.dto;

import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationDurationEnum;
import jakarta.validation.constraints.NotNull;

public record ReservationRequestDTO(
    @NotNull(message = "El copyId es requerido")
    Long copyId,
    @NotNull(message = "El userId es requerido")
    Long userId,
    @NotNull(message = "La duración es requerida")
    ReservationDurationEnum duration
) {
}
