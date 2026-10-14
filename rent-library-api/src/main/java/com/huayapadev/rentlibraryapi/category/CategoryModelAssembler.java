package com.huayapadev.rentlibraryapi.category;

import com.huayapadev.rentlibraryapi.category.controller.CategoryController;
import com.huayapadev.rentlibraryapi.category.dto.CategoryResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<CategoryResponseDTO, EntityModel<CategoryResponseDTO>> {

    @Override
    public EntityModel<CategoryResponseDTO> toModel(@NonNull CategoryResponseDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(CategoryController.class).getById(dto.id())).withSelfRel(),
            linkTo(CategoryController.class).withRel("categories")
        );
    }
}
