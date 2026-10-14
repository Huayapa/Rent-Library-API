package com.huayapadev.rentlibraryapi.copy.service;

import com.huayapadev.rentlibraryapi.loan.service.LoanService;
import com.huayapadev.rentlibraryapi.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CopyDeleteService {
    private final CopyService copyService;
    private final LoanService loanService;
    private final ReservationService reservationService;

    @Transactional
    public void delete(Long id) {
        copyService.verifyExistsById(id);
        copyService.delete(id);
    }

    // NOTE: Por si deseas eliminar copias de forma permanente "No se recomienda por historial de prestamos"
    @Transactional
    public void deleteForce(Long id) {
        copyService.verifyExistsByIdIgnoringSoftDelete(id);
        loanService.verifyCopyNotInUse(id, "No se puede eliminar la copia");
        reservationService.verifyCopyNotInUse(id, "No se puede eliminar la copia");
        copyService.deleteForce(id);
    }
}
