package com.huayapadev.rentlibraryapi.book.service;

import com.huayapadev.rentlibraryapi.author.model.AuthorModel;
import com.huayapadev.rentlibraryapi.author.service.AuthorService;
import com.huayapadev.rentlibraryapi.book.BookMapper;
import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.book.BookRepository;
import com.huayapadev.rentlibraryapi.book.BookSpecifications;
import com.huayapadev.rentlibraryapi.book.dto.BookFilterDTO;
import com.huayapadev.rentlibraryapi.book.dto.BookPatchRequestDTO;
import com.huayapadev.rentlibraryapi.book.dto.BookRequestDTO;
import com.huayapadev.rentlibraryapi.book.dto.BookResponseDTO;
import com.huayapadev.rentlibraryapi.bookcategory.model.BookCategoryModel;
import com.huayapadev.rentlibraryapi.bookcategory.service.BookCategoryService;
import com.huayapadev.rentlibraryapi.copy.service.CopyService;
import com.huayapadev.rentlibraryapi.publisher.model.PublisherModel;
import com.huayapadev.rentlibraryapi.publisher.service.PublisherService;
import com.huayapadev.rentlibraryapi.shared.exception.AlreadyExistsException;
import com.huayapadev.rentlibraryapi.shared.exception.InvalidStateException;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookCategoryService bookCategoryService;
    private final AuthorService authorService;
    private final CopyService copyService;
    private final PublisherService publisherService;
    private final BookMapper bookMapper;

    @Transactional
    public List<BookResponseDTO> getBooks(BookFilterDTO filter, Pageable pageable) {
        Specification<BookModel> spec = Specification
            .where(BookSpecifications.hasIsbn(filter.isbn()))
            .and(BookSpecifications.hasTitle(filter.title()))
            .and(BookSpecifications.hasAuthorId(filter.authorId()))
            .and(BookSpecifications.hasPublisherId(filter.publisherId()))
            .and(BookSpecifications.hasCategoryIds(filter.categoryIds()));

        return bookRepository.findAll(spec, pageable)
            .stream().map(bookMapper::toResponseDTO).toList();
    }

    @Transactional
    public BookResponseDTO getBookById(Long id) {
        BookModel book = getEntityById(id);
        return bookMapper.toResponseDTO(book);
    }

    public BookModel getEntityById(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO request) {
        Optional<BookModel> existing = bookRepository.findByIsbnIgnoringSoftDelete(request.isbn());

        BookModel book;
        if(existing.isPresent()) {
            book = existing.get();
            if(book.getDeletedAt() == null) {
                throw new AlreadyExistsException("El libro", "el ISBN", request.isbn());
            }
            book.setDeletedAt(null);
        } else {
            book = new BookModel();
        }

        AuthorModel author = authorService.getEntityById(request.authorId());
        PublisherModel publisher = publisherService.getEntityById(request.publisherId());

        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setReleaseData(request.releaseDate());
        book.setIsPublished(request.isPublished());
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setScheduledPublishAt(request.scheduledPublishAt());

        BookModel savedBook = bookRepository.save(book);
        List<BookCategoryModel> bookCategories = bookCategoryService.createCategoryIdsByBookId(savedBook, request.categoryIds());
        savedBook.setBookCategories(bookCategories);

        return bookMapper.toResponseDTO(savedBook);
    }

    @Transactional
    public BookResponseDTO patchBook(Long id, BookPatchRequestDTO request) {
        BookModel book = getEntityById(id);
        if(request.isbn().isPresent()) {
            book.setIsbn(request.isbn().get());
        }
        if(request.title().isPresent()) {
            book.setTitle(request.title().get());
        }
        if(request.description().isPresent()) {
            book.setDescription(request.description().get());
        }
        if(request.releaseDate().isPresent()) {
            book.setReleaseData(request.releaseDate().get());
        }
        if(request.isPublished().isPresent()) {
            book.setIsPublished(request.isPublished().get());
        }
        if(request.authorId().isPresent()) {
            AuthorModel author = authorService.getEntityById(request.authorId().get());
            book.setAuthor(author);
        }
        if(request.publisherId().isPresent()) {
            PublisherModel publisher = publisherService.getEntityById(request.publisherId().get());
            book.setPublisher(publisher);
        }
        if(request.scheduledPublishAt().isPresent()) {
            book.setScheduledPublishAt(request.scheduledPublishAt().get());
        }

        BookModel savedBook = bookRepository.save(book);

        if(request.categoryIds().isPresent()) {
            List<BookCategoryModel> bookCategories = bookCategoryService.updateCategoryIdsByBookId(book, request.categoryIds().get());
            savedBook.setBookCategories(bookCategories);
        }

        return bookMapper.toResponseDTO(savedBook);
    }

    @Transactional
    public BookResponseDTO restoreBook(Long id) {
        BookModel book = bookRepository.findByIdIgnoringSoftDelete(id)
            .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado, id: "+ id));
        if(book.getDeletedAt() == null) {
            throw new InvalidStateException("El libro con el id " + id + " no está eliminado");
        }
        book.setDeletedAt(null);
        BookModel savedBook = bookRepository.save(book);
        BookResponseDTO response = bookMapper.toResponseDTO(savedBook);
        copyService.restoreAllByBookId(savedBook.getId());
        return response;
    }

    @Transactional
    public void deleteBook(Long id) {
        if(!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado, id: "+ id);
        }
        copyService.softDeleteByBookId(id);
        bookRepository.softDeleteById(id);
    }
}
