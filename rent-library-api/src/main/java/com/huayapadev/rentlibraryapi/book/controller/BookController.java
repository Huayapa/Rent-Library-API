package com.huayapadev.rentlibraryapi.book.controller;

import com.huayapadev.rentlibraryapi.book.BookModelAssembler;
import com.huayapadev.rentlibraryapi.book.service.BookService;
import com.huayapadev.rentlibraryapi.book.dto.BookFilterDTO;
import com.huayapadev.rentlibraryapi.book.dto.BookPatchRequestDTO;
import com.huayapadev.rentlibraryapi.book.dto.BookRequestDTO;
import com.huayapadev.rentlibraryapi.book.dto.BookResponseDTO;
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
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<BookResponseDTO>>> getAll(
        @Valid @ModelAttribute BookFilterDTO filter,
        Pageable pageable
    ) {
        List<EntityModel<BookResponseDTO>> books = bookService.getBooks(filter, pageable)
            .stream().map(assembler::toModel).toList();
        Link selfLink = linkTo(methodOn(BookController.class).getAll(filter, pageable)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(books, selfLink));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BookResponseDTO>> getById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(assembler.toModel(book));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<BookResponseDTO>> create(@Valid @RequestBody BookRequestDTO request) {
        BookResponseDTO book = bookService.createBook(request);
        EntityModel<BookResponseDTO> model = assembler.toModel(book);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<BookResponseDTO>> patch(
        @PathVariable Long id,
        @Valid @RequestBody BookPatchRequestDTO request
    ) {
        BookResponseDTO book = bookService.patchBook(id, request);
        return ResponseEntity.ok(assembler.toModel(book));
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EntityModel<BookResponseDTO>> restore(@PathVariable Long id) {
        BookResponseDTO book = bookService.restoreBook(id);
        return ResponseEntity.ok(assembler.toModel(book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
