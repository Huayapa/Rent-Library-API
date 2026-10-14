package com.huayapadev.rentlibraryapi.loan.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record LoanUpdateDueAtRequestDTO(
    @NotNull(message = "la fecha de devolución es requerido")
    @Future(message = "La fecha debe ser futura a la fecha actual")
    Instant dueAt
) {
}
