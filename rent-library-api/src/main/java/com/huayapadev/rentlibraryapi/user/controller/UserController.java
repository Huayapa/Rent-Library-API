package com.huayapadev.rentlibraryapi.user.controller;

import com.huayapadev.rentlibraryapi.shared.dto.VerificationResponseDTO;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import com.huayapadev.rentlibraryapi.user.UserModelAssembler;
import com.huayapadev.rentlibraryapi.user.service.UserDeletionService;
import com.huayapadev.rentlibraryapi.user.service.UserService;
import com.huayapadev.rentlibraryapi.user.dto.*;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserDeletionService userDeletionService;
    private final UserModelAssembler assembler;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getAll(
        @Valid @ModelAttribute UserFilterDTO filter,
        Pageable pageable
    ) {
        List<EntityModel<UserResponseDTO>> users = userService.getAll(filter, pageable)
            .stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(UserController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(users, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<UserResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(userService.getById(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<EntityModel<UserResponseDTO>> getMy(@AuthenticationPrincipal UserModel user) {
        return ResponseEntity.ok(assembler.toModel(userService.getById(user.getId())));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO user = userService.create(request);
        EntityModel<UserResponseDTO> model = assembler.toModel(user);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<UserResponseDTO>> patch(
        @PathVariable Long id,
        @Valid @RequestBody UserPatchRequestDTO request
    ) {
        UserResponseDTO user = userService.patch(id, request);
        EntityModel<UserResponseDTO> model = assembler.toModel(user);
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/me")
    public ResponseEntity<EntityModel<UserResponseDTO>> patchMy(
        @AuthenticationPrincipal UserModel user,
        @Valid @RequestBody UserPatchRequestDTO request
    ) {
        UserResponseDTO newUser = userService.patch(user.getId(), request);
        EntityModel<UserResponseDTO> model = assembler.toModel(newUser);
        return ResponseEntity.ok(model);
    }

    /*
    CAMBIO DE CORREO
    *  Usuario = cambio de correo, verificación al nuevo correo, cambio, cerrar sesiones
    *  Admin = cambio de correo, cambio, cerrar sesiones
    */
    @PatchMapping("/me/email/change-request")
    public ResponseEntity<Void> requestEmailChange(
        @AuthenticationPrincipal UserModel ignoredUser
    ) {
        // TODO(Extra): Llamar al servicio de verificación y generar código y enviarlo al correo.
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/me/email/confirm")
    public ResponseEntity<VerificationResponseDTO> confirmEmailChange(
        @AuthenticationPrincipal UserModel user,
        @Valid @RequestBody UserConfirmEmailRequestDTO request
    ) {
        VerificationResponseDTO verification = userService.confirmEmail(user.getId(), request.code());
        return ResponseEntity.ok(verification);
    }

    @PatchMapping("/{id}/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<UserResponseDTO>> adminUpdateEmail(
        @AuthenticationPrincipal UserModel authenticatedUser,
        @PathVariable Long id,
        @Valid @RequestBody UserUpdateEmailRequestDTO request
    ) {
        UserResponseDTO user = userService.adminUpdateEmail(authenticatedUser,id, request.email(), true);
        EntityModel<UserResponseDTO> model = assembler.toModel(user);
        return ResponseEntity.ok(model);
    }

    /*
    CAMBIO DE CONTRASEÑA
    *  Usuario = cambio de contraseña, verificación a su correo, cambio, cerrar sesiones
    *  Admin = cambio de contraseña, cambio, cerrar sesiones
    */
    @PatchMapping("/me/password/change-request")
    public ResponseEntity<Void> requestPasswordChange() {
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/me/password/confirm")
    public ResponseEntity<Void> confirmPasswordChange() {
        return ResponseEntity.accepted().build();
    }

    /*
    CAMBIO DE ROL
    *  Admin = cambio de rol, cerrar sesiones
    */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<UserResponseDTO>> updateRole(
        @AuthenticationPrincipal UserModel authenticatedUser,
        @PathVariable Long id,
        @Valid @RequestBody UserUpdateRoleRequestDTO request
    ) {
        UserResponseDTO user = userService.updateRole(authenticatedUser, id, request.role());
        EntityModel<UserResponseDTO> model = assembler.toModel(user);
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<UserResponseDTO>> restore(
        @PathVariable Long id
    ) {
        UserResponseDTO user = userService.restore(id);
        EntityModel<UserResponseDTO> model = assembler.toModel(user);
        return ResponseEntity.ok(model);
    }

    /*
    ELIMINAR USUARIOS
    */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> delete(
        @AuthenticationPrincipal UserModel authenticatedUser,
        @PathVariable Long id
    ) {
        userDeletionService.deleteOther(authenticatedUser, id);
        return ResponseEntity.noContent().build();
    }

    //NOTE: Antes de borrarse a si mismo debe pasar por un endpoint de verificación con tiempo y aquí usarlo para realizar el borrado
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMy(
        @AuthenticationPrincipal UserModel user
    ) {
        userDeletionService.deleteSelf(user);
        return ResponseEntity.noContent().build();
    }

}
