package com.huayapadev.rentlibraryapi.shared.validation;

import com.huayapadev.rentlibraryapi.shared.interfaces.ValuedEnum;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class EnumExceptionUtils {

    public static boolean isEnum(String fieldClass) {
        try {
            Class<?> classEnum = Class.forName(fieldClass);
            return classEnum.isEnum();
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String valueEnums(Class<?> enumType) {
        return Arrays.stream(enumType.getEnumConstants()).map(e ->
            e instanceof ValuedEnum<?> valueEnum
            ? String.valueOf(valueEnum.displayValue())
            : e.toString()
        ).collect(Collectors.joining(", "));
    }

    public static String buildInvalidEnumMessage(Class<?> typeFieldEnum, String fieldName) {
        return "Argumento invalido de " + fieldName + ". Valores permitidos: " + valueEnums(typeFieldEnum);
    }
}
