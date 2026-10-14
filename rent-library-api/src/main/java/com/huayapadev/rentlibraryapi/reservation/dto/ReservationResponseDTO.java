package com.huayapadev.rentlibraryapi.reservation.dto;

import com.huayapadev.rentlibraryapi.copy.dto.CopyResponseDTO;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import com.huayapadev.rentlibraryapi.user.dto.UserResponseDTO;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;

@Relation(collectionRelation = "reservations", itemRelation = "reservation")
public record ReservationResponseDTO(
    Long id,
    CopyResponseDTO copy,
    UserResponseDTO user,
    Instant reservedAt,
    Instant expiresAt,
    ReservationStatusEnum status
) {
}
