package com.huayapadev.rentlibraryapi.loan.service;

import com.huayapadev.rentlibraryapi.loan.LoanMapper;
import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import com.huayapadev.rentlibraryapi.loan.dto.LoanResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanRestoreService {
    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @Transactional
    public LoanResponseDTO restore(Long id) {
        LoanModel loan = loanService.getDeletedById(id);
        LoanModel savedLoad = loanService.restore(loan);
        return loanMapper.toResponseDTO(savedLoad);
    }
}
