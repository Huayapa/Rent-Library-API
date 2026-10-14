package com.huayapadev.rentlibraryapi.reservation;

import com.huayapadev.rentlibraryapi.reservation.controller.ReservationController;
import com.huayapadev.rentlibraryapi.reservation.dto.ReservationResponseDTO;
import com.huayapadev.rentlibraryapi.shared.validation.RoleValidator;
import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class ReservationModelAssembler implements RepresentationModelAssembler<ReservationResponseDTO, EntityModel<ReservationResponseDTO>> {

    private final RoleValidator roleValidator;

    public EntityModel<ReservationResponseDTO> toModel(@NonNull ReservationResponseDTO dto) {
        EntityModel<ReservationResponseDTO> model = EntityModel.of(dto,
            linkTo(methodOn(ReservationController.class).getById(dto.id())).withSelfRel()
        );
        if(roleValidator.hasRole(UserRoleEnum.ADMIN) && roleValidator.hasRole(UserRoleEnum.LIBRARIAN)) {
            model.add(linkTo(methodOn(ReservationController.class)).withRel("reservations"));
        }
        return model;
    }
}
