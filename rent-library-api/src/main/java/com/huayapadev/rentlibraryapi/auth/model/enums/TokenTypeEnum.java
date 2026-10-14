package com.huayapadev.rentlibraryapi.auth.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenTypeEnum {
    BEARER("Bearer");

    private final String value;
}
