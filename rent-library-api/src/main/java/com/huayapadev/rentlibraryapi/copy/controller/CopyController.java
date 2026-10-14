package com.huayapadev.rentlibraryapi.copy.controller;

import com.huayapadev.rentlibraryapi.copy.CopyModelAssembler;
import com.huayapadev.rentlibraryapi.copy.service.CopyCreateService;
import com.huayapadev.rentlibraryapi.copy.service.CopyDeleteService;
import com.huayapadev.rentlibraryapi.copy.service.CopyService;
import com.huayapadev.rentlibraryapi.copy.dto.CopyFilterDTO;
import com.huayapadev.rentlibraryapi.copy.dto.CopyPatchRequestDTO;
import com.huayapadev.rentlibraryapi.copy.dto.CopyRequestDTO;
import com.huayapadev.rentlibraryapi.copy.dto.CopyResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/copies")
@PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
@RequiredArgsConstructor
public class CopyController {

    private final CopyService copyService;
    private final CopyCreateService copyCreateService;
    private final CopyDeleteService copyDeleteService;
    private final CopyModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CopyResponseDTO>>> getAll(
        @Valid @ModelAttribute CopyFilterDTO filter,
        Pageable pageable
    ) {
        List<EntityModel<CopyResponseDTO>> copies = copyService.getAll(filter, pageable)
            .stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(CopyController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(copies, selfLink));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CopyResponseDTO>> getById(@PathVariable Long id) {
        CopyResponseDTO copy = copyService.getById(id);
        return ResponseEntity.ok(assembler.toModel(copy));
    }

    @PostMapping
    public ResponseEntity<EntityModel<CopyResponseDTO>> create(@Valid @RequestBody CopyRequestDTO request) {
        CopyResponseDTO copy = copyCreateService.create(request);
        EntityModel<CopyResponseDTO> model = assembler.toModel(copy);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<CopyResponseDTO>> patch(
        @PathVariable Long id,
        @Valid @RequestBody CopyPatchRequestDTO request
    ) {
        CopyResponseDTO copy = copyService.patch(id, request);
        return ResponseEntity.ok(assembler.toModel(copy));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<EntityModel<CopyResponseDTO>> restore(@PathVariable Long id) {
        CopyResponseDTO copy = copyService.restore(id);
        return ResponseEntity.ok(assembler.toModel(copy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        copyDeleteService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
