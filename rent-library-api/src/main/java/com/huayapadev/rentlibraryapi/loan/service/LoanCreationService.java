package com.huayapadev.rentlibraryapi.loan.service;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.copy.service.CopyService;
import com.huayapadev.rentlibraryapi.loan.LoanMapper;
import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import com.huayapadev.rentlibraryapi.loan.dto.LoanRequestDTO;
import com.huayapadev.rentlibraryapi.loan.dto.LoanResponseDTO;
import com.huayapadev.rentlibraryapi.reservation.service.ReservationService;
import com.huayapadev.rentlibraryapi.shared.date.DateValidation;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import com.huayapadev.rentlibraryapi.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class LoanCreationService {
    private final LoanService loanService;
    private final LoanMapper loanMapper;
    private final CopyService copyService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final DateValidation dateValidation;

    @Transactional
    public LoanResponseDTO create(@Valid @RequestBody LoanRequestDTO request) {
        CopyModel copy = copyService.getEntityById(request.copyId());
        UserModel user = userService.getEntityById(request.userId());

        copyService.verifyIsAvailable(copy);
        dateValidation.verifyIsFuture(request.dueAt(), "La fecha de vencimiento");
        reservationService.processPendingForLoan(copy.getId(), user.getId());

        CopyModel savedCopy = copyService.markAsLoaned(copy);
        LoanModel savedLoan = loanService.create(savedCopy, user, request.dueAt());
        return loanMapper.toResponseDTO(savedLoan);
    }
}
