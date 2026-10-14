package com.huayapadev.rentlibraryapi.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReservationRequestExtendDTO(
    @NotNull(message = "El campo es requerido")
    @Future(message = "La fecha debe ser futura a la actual")
    Instant newExpiredAt
) {
}
