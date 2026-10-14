package com.huayapadev.rentlibraryapi.reservation.service;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.copy.service.CopyService;
import com.huayapadev.rentlibraryapi.loan.service.LoanService;
import com.huayapadev.rentlibraryapi.reservation.ReservationMapper;
import com.huayapadev.rentlibraryapi.reservation.model.ReservationModel;
import com.huayapadev.rentlibraryapi.reservation.dto.ReservationRequestDTO;
import com.huayapadev.rentlibraryapi.reservation.dto.ReservationResponseDTO;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import com.huayapadev.rentlibraryapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationCreationService {

    private final CopyService copyService;
    private final UserService userService;
    private final LoanService loanService;
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationResponseDTO create(ReservationRequestDTO request) {
        CopyModel copy = copyService.getEntityById(request.copyId());
        UserModel user = userService.getEntityById(request.userId());

        copyService.verifyIsAvailable(copy);
        reservationService.verifyNotExceedMaxActive(user.getId());
        reservationService.verifyNotExistsCopyAndStatusPending(copy.getId());
        loanService.verifyUserCanBorrow(user.getId());
        copyService.markAsReserved(copy);

        ReservationModel savedReservation = reservationService.create(copy, user, request.duration());
        return reservationMapper.toResponseDTO(savedReservation);
    }
}
