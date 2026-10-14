package com.huayapadev.rentlibraryapi.loan.dto;

import com.huayapadev.rentlibraryapi.loan.model.enums.LoanStatusEnum;

import java.time.Instant;

public record MyLoanFilterDTO(
    Long copyId,
    LoanStatusEnum status,
    Boolean isReturned,
    Boolean isOverdue,
    Instant loanedAfter,
    Instant loanedBefore
) {
}
