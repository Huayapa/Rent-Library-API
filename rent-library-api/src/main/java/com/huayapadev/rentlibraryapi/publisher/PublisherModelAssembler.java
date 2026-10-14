package com.huayapadev.rentlibraryapi.publisher;

import com.huayapadev.rentlibraryapi.publisher.controller.PublisherController;
import com.huayapadev.rentlibraryapi.publisher.dto.PublisherResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PublisherModelAssembler implements
    RepresentationModelAssembler<PublisherResponseDTO, EntityModel<PublisherResponseDTO>> {
    @Override
    public EntityModel<PublisherResponseDTO> toModel(@NonNull PublisherResponseDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(PublisherController.class).getById(dto.id())).withSelfRel(),
            linkTo(methodOn(PublisherController.class)).withRel("publishers")
        );
    }
}
