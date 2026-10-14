package com.huayapadev.rentlibraryapi.user.dto;

import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;

public record UserFilterDTO(
    String dni,
    String name,
    String email,
    Boolean isDeleted,
    UserRoleEnum role
) {
}
