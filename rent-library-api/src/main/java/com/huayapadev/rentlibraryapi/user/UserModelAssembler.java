package com.huayapadev.rentlibraryapi.user;

import com.huayapadev.rentlibraryapi.user.controller.UserController;
import com.huayapadev.rentlibraryapi.user.dto.UserResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDTO, EntityModel<UserResponseDTO>> {

    @Override
    public EntityModel<UserResponseDTO> toModel(@NonNull UserResponseDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(UserController.class).getById(dto.id())).withSelfRel()
        );
    }
}
