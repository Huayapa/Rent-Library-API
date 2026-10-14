package com.huayapadev.rentlibraryapi.author.controller;

import com.huayapadev.rentlibraryapi.author.AuthorModelAssembler;
import com.huayapadev.rentlibraryapi.author.service.AuthorService;
import com.huayapadev.rentlibraryapi.author.dto.AuthorFilterDTO;
import com.huayapadev.rentlibraryapi.author.dto.AuthorPatchRequestDTO;
import com.huayapadev.rentlibraryapi.author.dto.AuthorRequestDTO;
import com.huayapadev.rentlibraryapi.author.dto.AuthorResponseDTO;
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
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<AuthorResponseDTO>>> getAll(
        @Valid @ModelAttribute AuthorFilterDTO filter,
        Pageable pageable
    ) {
        List<EntityModel<AuthorResponseDTO>> authors = authorService.getAll(filter.name(), pageable)
            .stream()
            .map(assembler::toModel).toList();

        Link selfLink = linkTo(methodOn(AuthorController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(authors, selfLink));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AuthorResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(authorService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<AuthorResponseDTO>> create(@Valid @RequestBody AuthorRequestDTO request) {
        AuthorResponseDTO author = authorService.create(request);
        EntityModel<AuthorResponseDTO> model = assembler.toModel(author);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<AuthorResponseDTO>> patch(
        @PathVariable Long id,
        @Valid @RequestBody AuthorPatchRequestDTO request
    ) {
        AuthorResponseDTO author = authorService.patch(id, request);
        EntityModel<AuthorResponseDTO> model = assembler.toModel(author);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
