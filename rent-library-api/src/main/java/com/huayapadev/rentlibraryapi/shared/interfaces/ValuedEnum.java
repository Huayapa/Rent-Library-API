package com.huayapadev.rentlibraryapi.shared.interfaces;


public interface ValuedEnum<T> {
    T getValue();

    default String displayValue() {
        return String.valueOf(getValue());
    }
}