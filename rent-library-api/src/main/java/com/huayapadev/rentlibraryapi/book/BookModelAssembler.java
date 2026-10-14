package com.huayapadev.rentlibraryapi.book;

import com.huayapadev.rentlibraryapi.book.controller.BookController;
import com.huayapadev.rentlibraryapi.book.dto.BookResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<BookResponseDTO, EntityModel<BookResponseDTO>> {
    @Override
    public EntityModel<BookResponseDTO> toModel(@NonNull BookResponseDTO dto) {

        return EntityModel.of(dto,
            linkTo(methodOn(BookController.class).getById(dto.id())).withSelfRel(),
            linkTo(methodOn(BookController.class)).withRel("books")
        );
    }
}
