package com.huayapadev.rentlibraryapi.reservation;

import com.huayapadev.rentlibraryapi.reservation.model.ReservationModel;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import com.huayapadev.rentlibraryapi.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ReservationExpirationJob {
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    // NOTE: A mayor escala seria mejor usar una paginación para traerlo por partes.
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void markOverdueLoans() {
        List<ReservationModel> reservationsExpired =
            reservationRepository.findByStatusAndExpiresAtBefore(ReservationStatusEnum.PENDING, Instant.now());
        reservationsExpired.forEach(reservation -> reservationService.expire(reservation.getId()));
    }
}
