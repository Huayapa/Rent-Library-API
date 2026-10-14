package com.huayapadev.rentlibraryapi.category.controller;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.huayapadev.rentlibraryapi.category.CategoryModelAssembler;
import com.huayapadev.rentlibraryapi.category.dto.CategoryFilterDTO;
import com.huayapadev.rentlibraryapi.category.dto.CategoryRequestDTO;
import com.huayapadev.rentlibraryapi.category.dto.CategoryResponseDTO;
import com.huayapadev.rentlibraryapi.category.service.CategoryService;
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

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoryResponseDTO>>> getAll(
        @Valid @ModelAttribute CategoryFilterDTO filter,
        Pageable pageable
    ) {
        List<EntityModel<CategoryResponseDTO>> categories = categoryService.getAll(filter.name(), pageable).stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(CategoryController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(categories, selfLink));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoryResponseDTO>> getById(@PathVariable Long id) {
        return  ResponseEntity.ok(assembler.toModel(categoryService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<CategoryResponseDTO>> create(@Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO response = categoryService.create(request);
        EntityModel<CategoryResponseDTO> model = assembler.toModel(response);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<CategoryResponseDTO>> put(
        @PathVariable Long id,
        @Valid @RequestBody CategoryRequestDTO request
    ) {
        CategoryResponseDTO response = categoryService.put(id, request);
        EntityModel<CategoryResponseDTO> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
