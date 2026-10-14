package com.huayapadev.rentlibraryapi.loan.dto;

import com.huayapadev.rentlibraryapi.copy.dto.CopyResponseDTO;
import com.huayapadev.rentlibraryapi.loan.model.enums.LoanStatusEnum;
import com.huayapadev.rentlibraryapi.user.dto.UserResponseDTO;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;

@Relation(collectionRelation = "loans", itemRelation = "loan")
public record LoanResponseDTO(
    Long id,
    CopyResponseDTO copy,
    UserResponseDTO user,
    Instant loanedAt,
    Instant dueAt,
    Instant returnedAt,
    LoanStatusEnum status
) {
}
