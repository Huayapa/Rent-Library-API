package com.huayapadev.rentlibraryapi.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidEnumValidator implements ConstraintValidator<ValidEnum, String> {
    private Set<String> validValues;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        validValues = Arrays.stream(constraintAnnotation.enumClass()
            .getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || validValues.contains(value);
    }
}
