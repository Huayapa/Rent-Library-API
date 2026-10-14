package com.huayapadev.rentlibraryapi.publisher.controller;

import com.huayapadev.rentlibraryapi.publisher.PublisherModelAssembler;
import com.huayapadev.rentlibraryapi.publisher.service.PublisherService;
import com.huayapadev.rentlibraryapi.publisher.dto.PublisherRequestDTO;
import com.huayapadev.rentlibraryapi.publisher.dto.PublisherResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;
    private final PublisherModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PublisherResponseDTO>>> getAll(
        @RequestParam(required = false)
        String name,
        Pageable pageable
    ) {
        List<EntityModel<PublisherResponseDTO>> publishers = publisherService.getAll(name, pageable)
            .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(publishers,
            linkTo(methodOn(PublisherController.class).getAll(name, pageable)).withSelfRel()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PublisherResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(publisherService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<PublisherResponseDTO>> create(
        @Valid @RequestBody PublisherRequestDTO request
    ) {
        PublisherResponseDTO publisher = publisherService.create(request);
        EntityModel<PublisherResponseDTO> model = assembler.toModel(publisher);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<PublisherResponseDTO>> put(
        @PathVariable Long id,
        @Valid @RequestBody PublisherRequestDTO request
    ) {
        PublisherResponseDTO publisher = publisherService.put(id, request);
        EntityModel<PublisherResponseDTO> model = assembler.toModel(publisher);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publisherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
