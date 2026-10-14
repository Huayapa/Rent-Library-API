package com.huayapadev.rentlibraryapi.loan.controller;

import com.huayapadev.rentlibraryapi.loan.LoanMapper;
import com.huayapadev.rentlibraryapi.loan.LoanModelAssembler;
import com.huayapadev.rentlibraryapi.loan.service.*;
import com.huayapadev.rentlibraryapi.loan.dto.*;
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
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final LoanCreationService loanCreationService;
    private final LoanRestoreService loanRestoreService;
    private final LoanDeleteService loanDeleteService;
    private final LoanCancelService loanCancelService;
    private final LoanModelAssembler assembler;
    private final LoanMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<CollectionModel<EntityModel<LoanResponseDTO>>> getAll(
        @Valid @ModelAttribute LoanFilterDTO filter,
        Pageable pageable
    ) {
        List<EntityModel<LoanResponseDTO>> loans = loanService.getAll(filter, pageable)
            .stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(LoanController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(loans, selfLink));
    }

    @GetMapping("/me")
    public ResponseEntity<CollectionModel<EntityModel<LoanResponseDTO>>> getMyAll(
        @AuthenticationPrincipal UserModel user,
        @Valid @ModelAttribute MyLoanFilterDTO myFilter,
        Pageable pageable
    ) {
        LoanFilterDTO filter = mapper.toFilterDTO(myFilter, user.getId());
        List<EntityModel<LoanResponseDTO>> loans = loanService.getAll(filter, pageable)
            .stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(LoanController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(loans, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<LoanResponseDTO>> getById(@PathVariable Long id) {
        LoanResponseDTO loan = loanService.getById(id);
        return ResponseEntity.ok(assembler.toModel(loan));
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<EntityModel<LoanResponseDTO>> getMyById(
        @AuthenticationPrincipal UserModel user,
        @PathVariable Long id
    ) {
        LoanResponseDTO loan = loanService.getMyById(user.getId(), id);
        return ResponseEntity.ok(assembler.toModel(loan));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<LoanResponseDTO>> create(@Valid @RequestBody LoanRequestDTO request) {
        LoanResponseDTO loan = loanCreationService.create(request);
        EntityModel<LoanResponseDTO> model = assembler.toModel(loan);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<LoanResponseDTO>> restore(@PathVariable Long id) {
        LoanResponseDTO loan = loanRestoreService.restore(id);
        return ResponseEntity.ok(assembler.toModel(loan));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<LoanResponseDTO>> cancel(@PathVariable Long id) {
        LoanResponseDTO loan = loanCancelService.cancel(id);
        return ResponseEntity.ok(assembler.toModel(loan));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        loanDeleteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<LoanResponseDTO>> updateReturnedAt(@PathVariable Long id) {
        LoanResponseDTO loan = loanService.updateReturnedAt(id);
        return ResponseEntity.ok(assembler.toModel(loan));
    }

    @PatchMapping("/{id}/extend")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<LoanResponseDTO>> updateDueAt(
        @PathVariable Long id,
        @Valid @RequestBody LoanUpdateDueAtRequestDTO request
        ) {
        LoanResponseDTO loan = loanService.updateDueAt(id, request.dueAt());
        return ResponseEntity.ok(assembler.toModel(loan));
    }
}
