package com.huayapadev.rentlibraryapi.book;

import com.huayapadev.rentlibraryapi.author.AuthorMapper;
import com.huayapadev.rentlibraryapi.book.dto.BookResponseDTO;
import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.bookcategory.model.BookCategoryModel;
import com.huayapadev.rentlibraryapi.category.CategoryMapper;
import com.huayapadev.rentlibraryapi.publisher.PublisherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final AuthorMapper authorMapper;
    private final PublisherMapper publisherMapper;
    private final CategoryMapper categoryMapper;
    public BookResponseDTO toResponseDTO(BookModel book) {
        return new BookResponseDTO(
            book.getId(),
            book.getIsbn(),
            book.getTitle(),
            book.getDescription(),
            book.getReleaseData(),
            book.getIsPublished(),
            authorMapper.toResponseDTO(book.getAuthor()),
            publisherMapper.toResponseDTO(book.getPublisher()),
            book.getScheduledPublishAt(),
            book.getCreatedAt(),
            book.getUpdatedAt(),
            book.getBookCategories()
                .stream()
                .map(BookCategoryModel::getCategory)
                .map(categoryMapper::toResponseDTO)
                .toList()
        );
    }
}
