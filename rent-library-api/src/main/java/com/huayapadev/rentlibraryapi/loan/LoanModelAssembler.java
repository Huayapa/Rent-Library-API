package com.huayapadev.rentlibraryapi.loan;

import com.huayapadev.rentlibraryapi.loan.controller.LoanController;
import com.huayapadev.rentlibraryapi.loan.dto.LoanResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoanModelAssembler implements RepresentationModelAssembler<LoanResponseDTO, EntityModel<LoanResponseDTO>> {
    @Override
    public EntityModel<LoanResponseDTO> toModel(@NonNull LoanResponseDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(LoanController.class).getById(dto.id())).withSelfRel(),
            linkTo(methodOn(LoanController.class)).withRel("loans")
        );
    }
}
