package com.huayapadev.rentlibraryapi.reservation.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.huayapadev.rentlibraryapi.shared.interfaces.ValuedEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationDurationEnum implements ValuedEnum<Integer> {
    TODAY(1),
    THREE_DAYS(3),
    FIVE_DAYS(5),
    SEVEN_DAYS(7);

    private final int days;

    @JsonValue
    @Override
    public Integer getValue() {
        return days;
    }
}
