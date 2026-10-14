package com.huayapadev.rentlibraryapi.reservation;

import com.huayapadev.rentlibraryapi.copy.CopyMapper;
import com.huayapadev.rentlibraryapi.reservation.dto.*;
import com.huayapadev.rentlibraryapi.reservation.model.ReservationModel;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import com.huayapadev.rentlibraryapi.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ReservationMapper {
    private final CopyMapper copyMapper;
    private final UserMapper userMapper;
    public ReservationResponseDTO toResponseDTO(ReservationModel reservation) {
        return new ReservationResponseDTO(
            reservation.getId(),
            copyMapper.toResponseDTO(reservation.getCopy()),
            userMapper.toResponseDTO(reservation.getUser()),
            reservation.getReservedAt(),
            reservation.getExpiresAt(),
            displayStatus(reservation)
        );
    }

    public ReservationRequestDTO toRequestDTO(MyReservationRequestDTO reservation, Long userId) {
        return new ReservationRequestDTO(
            reservation.copyId(),
            userId,
            reservation.duration()
        );
    }

    public ReservationFilterDTO toFilterDTO(MyReservationFilterDTO myReservation, Long userId) {
        return new ReservationFilterDTO(
            userId,
            myReservation.reservedAfter(),
            myReservation.reservedBefore(),
            myReservation.expiresAfter(),
            myReservation.expiresBefore(),
            myReservation.status()
        );
    }

    private ReservationStatusEnum displayStatus(ReservationModel reservation) {
        if(reservation.getStatus() == ReservationStatusEnum.PENDING && reservation.getExpiresAt().isBefore(Instant.now()))
            return ReservationStatusEnum.EXPIRED;
        return reservation.getStatus();
    }
}
