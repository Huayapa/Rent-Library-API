package com.huayapadev.rentlibraryapi.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* NOTE: Dejado de usar ya que funciona cuando el valor es String y pierde que mi request tenga el tipo de dato correcto
*  Se puede solucionar con una conversión pero lo veo innecesario, ademas que puedo validarlo en el globalException de forma general
* CONSTRAINT: Restringen las reglas de un objeto para que sea válido
*/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEnumValidator.class)
public @interface ValidEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "El campo debe ser un valor valido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
