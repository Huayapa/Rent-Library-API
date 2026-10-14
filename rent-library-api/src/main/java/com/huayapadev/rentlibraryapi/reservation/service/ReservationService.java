package com.huayapadev.rentlibraryapi.reservation.service;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.copy.service.CopyService;
import com.huayapadev.rentlibraryapi.copy.model.enums.CopyStatusEnum;
import com.huayapadev.rentlibraryapi.reservation.*;
import com.huayapadev.rentlibraryapi.reservation.dto.ReservationFilterDTO;
import com.huayapadev.rentlibraryapi.reservation.dto.ReservationResponseDTO;
import com.huayapadev.rentlibraryapi.reservation.model.ReservationModel;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationDurationEnum;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import com.huayapadev.rentlibraryapi.shared.exception.InvalidStateException;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    @Value("${app.library.rules.max-active-reservation}")
    private int maxActiveReservation;

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final CopyService copyService;

    @Transactional
    public List<ReservationResponseDTO> getAll(ReservationFilterDTO filter, Pageable pageable) {
        Specification<ReservationModel> spec = Specification
            .where(ReservationSpecification.hasUserId(filter.userId()))
            .and(ReservationSpecification.hasStatus(filter.status()))
            .and(ReservationSpecification.reservedAfter(filter.reservedAfter()))
            .and(ReservationSpecification.reservedBefore(filter.reservedBefore()))
            .and(ReservationSpecification.expiresAfter(filter.expiresAfter()))
            .and(ReservationSpecification.expiresBefore(filter.expiresBefore()));
        return reservationRepository.findAll(spec, pageable)
            .stream()
            .map(reservationMapper::toResponseDTO)
            .toList();
    }

    @Transactional
    public ReservationResponseDTO getById(Long id) {
        ReservationModel reservation = getEntityById(id);
        return reservationMapper.toResponseDTO(reservation);
    }

    @Transactional
    public ReservationResponseDTO getMyById(Long userId, Long id) {
        ReservationModel reservation = getEntityByIdAndUserId(id, userId);
        return reservationMapper.toResponseDTO(reservation);
    }

    @Transactional
    public void cancel(Long id) {
        ReservationModel reservation = getEntityById(id);
        applyCancelReservation(reservation);
    }

    @Transactional
    public void cancelMy(Long userId, Long id) {
        ReservationModel reservation = getEntityByIdAndUserId(id, userId);
        applyCancelReservation(reservation);
    }

    @Transactional
    public void cancelAllByUserId(Long userId) {
        reservationRepository.cancelByUserIdAndStatusPending(userId);
    }

    private void applyCancelReservation(ReservationModel reservation) {
        if(reservation.getStatus() != ReservationStatusEnum.PENDING) {
            throw new InvalidStateException("Solo se puede cancelar reservaciones pendientes");
        }
        reservation.setStatus(ReservationStatusEnum.CANCELLED);
        reservationRepository.save(reservation);
        if(reservation.getCopy().getStatus() == CopyStatusEnum.RESERVED) {
            copyService.markAsAvailable(reservation.getCopy().getId());
        }
    }

    @Transactional
    public ReservationResponseDTO extend(Long id, Instant newExpiredAt) {
        ReservationModel reservation = getEntityById(id);

        if (reservation.getStatus() != ReservationStatusEnum.PENDING) {
            throw new InvalidStateException("No se puede extender una reservación no pendiente.");
        }

        if(reservation.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidStateException("No se puede extender una reservación vencida.");
        }

        if (newExpiredAt.isBefore(reservation.getExpiresAt())) {
            throw new InvalidStateException("La nueva fecha debe ser posterior a la fecha de expiración actual.");
        }

        if(newExpiredAt.isBefore(Instant.now())) {
            throw new InvalidStateException("La fecha debe ser futura a la fecha actual");
        }

        reservation.setExpiresAt(newExpiredAt);
        ReservationModel savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toResponseDTO(savedReservation);
    }

    @Transactional
        public void delete(Long id) {
        ReservationModel reservation = getEntityById(id);
        deleteAndCancelReservation(reservation);
    }

    @Transactional
    public void deleteMy(Long userId, Long id) {
        ReservationModel reservation = getEntityByIdAndUserId(id, userId);
        deleteAndCancelReservation(reservation);
    }

    public void expire(Long id) {
        ReservationModel reservation = reservationRepository.findByIdAndExpiresAtBefore(id, Instant.now())
            .orElseThrow(() -> new ResourceNotFoundException("Reservación no encontrada con el id " + id));

        if(!reservation.getStatus().equals(ReservationStatusEnum.PENDING)) {
            throw new InvalidStateException("La reservación ya fue validada");
        }

        if(reservation.getExpiresAt().isAfter(Instant.now())) {
            throw new InvalidStateException("La reservación aun no esta vencida");
        }
        reservation.setStatus(ReservationStatusEnum.EXPIRED);
        copyService.markAsAvailable(reservation.getCopy().getId());
        reservationRepository.save(reservation);
    }

    public void processPendingForLoan(Long copyId, Long userId) {
        Optional<ReservationModel> exists = reservationRepository
            .findByCopyIdAndStatus(copyId, ReservationStatusEnum.PENDING);

        if(exists.isEmpty()) return;

        ReservationModel reservation = exists.get();

        if(reservation.getExpiresAt() != null && reservation.getExpiresAt().isBefore(Instant.now())) {
            reservation.setStatus(ReservationStatusEnum.CANCELLED);
            reservationRepository.save(reservation);
            return;
        }

        if (!reservation.getUser().getId().equals(userId)) {
            throw new InvalidStateException("Esta copia está reservada para otro usuario");
        }

        reservation.setStatus(ReservationStatusEnum.FULFILLED);
        reservationRepository.save(reservation);
    }

    private void deleteAndCancelReservation(ReservationModel reservation) {
        if(reservation.getStatus() == ReservationStatusEnum.PENDING) {
            CopyModel copy = reservation.getCopy();
            if(copy.getStatus() == CopyStatusEnum.RESERVED) {
                copyService.markAsAvailable(copy.getId());
            }

            reservation.setStatus(ReservationStatusEnum.CANCELLED);
        }
        reservation.setDeletedAt(Instant.now());
        reservationRepository.save(reservation);
    }

    public ReservationModel getEntityById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation no encontrado con el id " + id));
    }

    public ReservationModel getEntityByIdAndUserId(Long id, Long userId) {
        return reservationRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation no encontrado con el id " + id));
    }

    public void verifyNotExceedMaxActive(Long userId) {
        long reservationActive = reservationRepository.countPendingByUserId(userId);
        if(reservationActive >= maxActiveReservation) {
            throw new InvalidStateException("No puede reservar mas libros, limite excedido: " + maxActiveReservation);
        }
    }

    public void verifyNotExistsCopyAndStatusPending(Long copyId) {
        if(reservationRepository.existsByCopyIdAndStatus(copyId, ReservationStatusEnum.PENDING)) {
            throw new InvalidStateException("Esta copia ya tiene una reserva pendiente");
        }
    }

    public void verifyCopyNotInUse(Long copyId, String extendMessage) {
        if(reservationRepository.existsByCopyIdIgnoringSoftDelete(copyId)) {
            throw new InvalidStateException("La copia ya esta haciendo usada en una reservación, " + extendMessage);
        }
    }

    public ReservationModel create(CopyModel copy, UserModel user, ReservationDurationEnum duration) {
        Instant expiresAt = convertDurationDaysToInstant(duration);
        ReservationModel reservation = new ReservationModel();
        reservation.setCopy(copy);
        reservation.setUser(user);
        reservation.setStatus(ReservationStatusEnum.PENDING);
        reservation.setExpiresAt(expiresAt);
        return reservationRepository.save(reservation);
    }

    private Instant convertDurationDaysToInstant(ReservationDurationEnum duration) {
        return Instant.now().plus(duration.getValue(), ChronoUnit.DAYS);
    }
}
