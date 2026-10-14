package com.huayapadev.rentlibraryapi.user.dto;

import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRoleRequestDTO(
    @NotNull(message = "El rol es requerido.")
    UserRoleEnum role
) {
}
