package com.huayapadev.rentlibraryapi.loan;

import com.huayapadev.rentlibraryapi.copy.CopyMapper;
import com.huayapadev.rentlibraryapi.loan.dto.LoanFilterDTO;
import com.huayapadev.rentlibraryapi.loan.dto.MyLoanFilterDTO;
import com.huayapadev.rentlibraryapi.loan.dto.LoanResponseDTO;
import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import com.huayapadev.rentlibraryapi.loan.model.enums.LoanStatusEnum;
import com.huayapadev.rentlibraryapi.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LoanMapper {
    private final CopyMapper copyMapper;
    private final UserMapper userMapper;
    public LoanResponseDTO toResponseDTO(LoanModel loan) {
        return new LoanResponseDTO(
            loan.getId(),
            copyMapper.toResponseDTO(loan.getCopy()),
            userMapper.toResponseDTO(loan.getUser()),
            loan.getLoanedAt(),
            loan.getDueAt(),
            loan.getReturnedAt(),
            displayStatus(loan)
        );
    }

    public LoanFilterDTO toFilterDTO(MyLoanFilterDTO filter, Long userId) {
        return new LoanFilterDTO(
            filter.copyId(),
            userId,
            filter.status(),
            filter.isReturned(),
            filter.isOverdue(),
            filter.loanedAfter(),
            filter.loanedBefore()
        );
    }

    private LoanStatusEnum displayStatus(LoanModel loan) {
        LoanStatusEnum status = loan.getStatus();
        if (status == LoanStatusEnum.ACTIVE && loan.getDueAt().isBefore(Instant.now())) {
            return LoanStatusEnum.OVERDUE;
        }
        return status;
    }
}
