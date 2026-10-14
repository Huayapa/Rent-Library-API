package com.huayapadev.rentlibraryapi.reservation.dto;

import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import jakarta.validation.constraints.AssertTrue;

import java.time.Instant;

public record ReservationFilterDTO(
    Long userId,
    Instant reservedAfter,
    Instant reservedBefore,
    Instant expiresAfter,
    Instant expiresBefore,
    ReservationStatusEnum status
) {
    @AssertTrue(message = "expiresBefore debe ser posterior a expiresAfter")
    public boolean isExpiresRangeValid() {
        if(expiresAfter == null || expiresBefore == null) return true;
        return expiresBefore.isAfter(expiresAfter);
    }

    @AssertTrue(message = "expiresBefore debe ser posterior a expiresAfter")
    public boolean isReservedRangeValid() {
        if(reservedAfter == null || reservedBefore == null) return true;
        return reservedBefore.isAfter(reservedAfter);
    }
}
