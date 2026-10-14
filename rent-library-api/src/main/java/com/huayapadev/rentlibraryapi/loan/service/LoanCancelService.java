package com.huayapadev.rentlibraryapi.loan.service;

import com.huayapadev.rentlibraryapi.copy.service.CopyService;
import com.huayapadev.rentlibraryapi.loan.LoanMapper;
import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import com.huayapadev.rentlibraryapi.loan.dto.LoanResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanCancelService {
    private final LoanService loanService;
    private final CopyService copyService;
    private final LoanMapper loanMapper;

    @Transactional
    public LoanResponseDTO cancel(Long id) {
        LoanModel loan = loanService.getEntityById(id);
        loanService.verifyCanBeCancel(loan);
        LoanModel savedLoan = loanService.cancel(loan);
        copyService.markAsAvailable(loan.getCopy().getId());
        return loanMapper.toResponseDTO(savedLoan);
    }
}
