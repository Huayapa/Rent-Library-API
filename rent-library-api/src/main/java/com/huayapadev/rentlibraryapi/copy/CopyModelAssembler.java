package com.huayapadev.rentlibraryapi.copy;

import com.huayapadev.rentlibraryapi.copy.controller.CopyController;
import com.huayapadev.rentlibraryapi.copy.dto.CopyResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CopyModelAssembler implements RepresentationModelAssembler<CopyResponseDTO, EntityModel<CopyResponseDTO>> {
    @Override
    public EntityModel<CopyResponseDTO> toModel(@NonNull CopyResponseDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(CopyController.class).getById(dto.id())).withSelfRel(),
            linkTo(methodOn(CopyController.class)).withRel("copies")
        );
    }
}
