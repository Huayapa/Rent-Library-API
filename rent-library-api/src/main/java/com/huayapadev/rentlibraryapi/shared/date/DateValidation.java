package com.huayapadev.rentlibraryapi.shared.date;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DateValidation {
    public void verifyIsFuture(Instant date, String message) {
        if(date.isBefore(Instant.now())) {
            throw new IllegalArgumentException(message + " debe ser futura");
        }
    }
}
