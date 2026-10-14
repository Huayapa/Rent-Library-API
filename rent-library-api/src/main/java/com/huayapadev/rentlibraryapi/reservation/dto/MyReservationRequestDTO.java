package com.huayapadev.rentlibraryapi.reservation.dto;

import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationDurationEnum;
import jakarta.validation.constraints.NotNull;

public record MyReservationRequestDTO(
    @NotNull(message = "El copyId es requerido")
    Long copyId,

    @NotNull(message = "La duración es requerida")
    ReservationDurationEnum duration
) {
}
