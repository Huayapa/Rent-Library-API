package com.huayapadev.rentlibraryapi.loan.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record LoanRequestDTO(
    @NotNull(message = "El copyId es requerido")
    Long copyId,
    @NotNull(message = "El userId es requerido")
    Long userId,
    @NotNull(message = "La fecha de entrega es requerida")
    @Future(message = "La fecha de vencimiento debe ser futura")
    Instant dueAt
) {
}
