package com.huayapadev.rentlibraryapi.user.dto;

import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "users", itemRelation = "user")
public record UserResponseDTO(
    Long id,
    String dni,
    String name,
    String email,
    UserRoleEnum role
) {
}
