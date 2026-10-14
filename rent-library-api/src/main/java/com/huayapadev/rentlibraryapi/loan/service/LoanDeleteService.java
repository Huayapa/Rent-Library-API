package com.huayapadev.rentlibraryapi.loan.service;

import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanDeleteService {
    private final LoanService loanService;

    @Transactional
    public void delete(Long id) {
        LoanModel loan = loanService.getEntityById(id);
        loanService.verifyCanBeDeleted(loan);
        loanService.delete(loan);
    }
}
