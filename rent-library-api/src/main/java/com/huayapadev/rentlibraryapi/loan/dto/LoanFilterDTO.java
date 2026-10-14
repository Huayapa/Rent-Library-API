package com.huayapadev.rentlibraryapi.loan.dto;

import com.huayapadev.rentlibraryapi.loan.model.enums.LoanStatusEnum;

import java.time.Instant;

public record LoanFilterDTO(
    Long copyId,
    Long userId,
    LoanStatusEnum status,
    Boolean isReturned,
    Boolean isOverdue,
    Instant loanedAfter,
    Instant loanedBefore
) {
}
