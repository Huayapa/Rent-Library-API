package com.huayapadev.rentlibraryapi.user.service;

import com.huayapadev.rentlibraryapi.auth.service.AuthService;
import com.huayapadev.rentlibraryapi.loan.service.LoanService;
import com.huayapadev.rentlibraryapi.reservation.service.ReservationService;
import com.huayapadev.rentlibraryapi.shared.exception.InvalidStateException;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeletionService {
    private final UserService userService;
    private final LoanService loanService;
    private final ReservationService reservationService;
    private final AuthService authService;

    @Transactional
    public void deleteOther(UserModel authenticatedUser, Long userId) {
        UserModel user = userService.getEntityByIdIgnoringSoftDelete(userId);
        if(authenticatedUser.getId().equals(user.getId())) {
            throw new InvalidStateException("No se puede eliminarse a si mismo, ya que esto es para eliminar otros usuarios");
        }
        if(user.getRole().isHigherOrEqualThan(authenticatedUser.getRole())) {
            throw new InvalidStateException("No puede eliminar un usuario con rol igual o superior");
        }
        delete(user, "no puedes eliminar esta cuenta");
    }

    @Transactional
    public void deleteSelf(UserModel user) {
        delete(user, "no puedes eliminar tu cuenta");
    }

    private void delete(UserModel user, String extendMessage) {
        loanService.verifyNotActiveLoans(user.getId(), extendMessage);
        reservationService.cancelAllByUserId(user.getId());
        authService.revokeAllSessions(user);
        userService.softDelete(user.getId());
    }
}
