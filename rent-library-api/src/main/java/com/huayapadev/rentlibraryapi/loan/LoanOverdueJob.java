package com.huayapadev.rentlibraryapi.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LoanOverdueJob {
    private final LoanRepository loanRepository;

    @Scheduled(fixedRate = 6, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void markOverdueLoans() {
        loanRepository.markOverdueLoans(Instant.now());
    }
}
