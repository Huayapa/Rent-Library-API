package com.huayapadev.rentlibraryapi.author;

import com.huayapadev.rentlibraryapi.author.controller.AuthorController;
import com.huayapadev.rentlibraryapi.author.dto.AuthorResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthorModelAssembler
    implements RepresentationModelAssembler<AuthorResponseDTO, EntityModel<AuthorResponseDTO>> {

    @Override
    public EntityModel<AuthorResponseDTO> toModel(@NonNull AuthorResponseDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(AuthorController.class).getById(dto.id())).withSelfRel(),
            linkTo(AuthorController.class).withRel("authors")
        );
    }
}
