package com.huayapadev.rentlibraryapi.reservation.controller;

import com.huayapadev.rentlibraryapi.reservation.ReservationMapper;
import com.huayapadev.rentlibraryapi.reservation.ReservationModelAssembler;
import com.huayapadev.rentlibraryapi.reservation.service.ReservationCreationService;
import com.huayapadev.rentlibraryapi.reservation.service.ReservationService;
import com.huayapadev.rentlibraryapi.reservation.dto.*;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationCreationService reservationCreationService;
    private final ReservationModelAssembler assembler;
    private final ReservationMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<CollectionModel<EntityModel<ReservationResponseDTO>>> getAll(
        @Valid
        @ModelAttribute
        ReservationFilterDTO filter,
        Pageable pageable
    ) {
        List<EntityModel<ReservationResponseDTO>> reservations = reservationService.getAll(filter, pageable)
            .stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(ReservationController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(reservations, selfLink));
    }

    @GetMapping("/me")
    public ResponseEntity<CollectionModel<EntityModel<ReservationResponseDTO>>> getMyAll(
        @Valid
        @ModelAttribute
        MyReservationFilterDTO myFilter,
        @AuthenticationPrincipal UserModel user,
        Pageable pageable
    ) {
        ReservationFilterDTO filter = mapper.toFilterDTO(myFilter, user.getId());
        List<EntityModel<ReservationResponseDTO>> reservations = reservationService.getAll(filter, pageable)
            .stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(ReservationController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(reservations, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<ReservationResponseDTO>> getById(@PathVariable Long id) {
        ReservationResponseDTO reservation = reservationService.getById(id);
        return ResponseEntity.ok(assembler.toModel(reservation));
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<EntityModel<ReservationResponseDTO>> getMyById(
        @AuthenticationPrincipal UserModel user,
        @PathVariable Long id
    ) {
        ReservationResponseDTO reservation = reservationService.getMyById(user.getId(), id);
        return ResponseEntity.ok(assembler.toModel(reservation));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<ReservationResponseDTO>> create(@Valid @RequestBody ReservationRequestDTO request) {
        ReservationResponseDTO reservation = reservationCreationService.create(request);
        EntityModel<ReservationResponseDTO> model = assembler.toModel(reservation);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PostMapping("/me")
    public ResponseEntity<EntityModel<ReservationResponseDTO>> createMy(
        @AuthenticationPrincipal UserModel user,
        @Valid @RequestBody MyReservationRequestDTO myRequest
    ) {
        ReservationRequestDTO request = mapper.toRequestDTO(myRequest, user.getId());
        ReservationResponseDTO reservation = reservationCreationService.create(request);
        EntityModel<ReservationResponseDTO> model = assembler.toModel(reservation);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/{id}")
    public ResponseEntity<Void> deleteMy(@AuthenticationPrincipal UserModel user, @PathVariable Long id) {
        reservationService.deleteMy(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/{id}/cancel")
    public ResponseEntity<Void> cancelMy(@AuthenticationPrincipal UserModel user, @PathVariable Long id) {
        reservationService.cancelMy(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/extend")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<ReservationResponseDTO>> extend(
        @PathVariable Long id,
        @Valid @RequestBody ReservationRequestExtendDTO request
    ) {
        ReservationResponseDTO reservation = reservationService.extend(id, request.newExpiredAt());
        return ResponseEntity.ok(assembler.toModel(reservation));
    }
}
