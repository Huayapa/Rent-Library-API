package com.huayapadev.rentlibraryapi.bookcategory.service;

import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.bookcategory.model.BookCategoryModel;
import com.huayapadev.rentlibraryapi.bookcategory.BookCategoryRepository;
import com.huayapadev.rentlibraryapi.category.model.CategoryModel;
import com.huayapadev.rentlibraryapi.category.service.CategoryService;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;
    private final CategoryService categoryService;

    public List<BookCategoryModel> saveAll(List<BookCategoryModel> bookCategories) {
        return bookCategoryRepository.saveAll(bookCategories);
    }

    @Transactional
    public List<BookCategoryModel> createCategoryIdsByBookId(BookModel book, List<Long> categoryIds) {
        List<CategoryModel> categories = getAllCategoryByIds(categoryIds);
        return saveAll(buildBookCategories(book, categories));
    }

    @Transactional
    public List<BookCategoryModel> updateCategoryIdsByBookId(BookModel book, List<Long> categoryIds) {
        bookCategoryRepository.deleteByBookId(book.getId());
        return createCategoryIdsByBookId(book, categoryIds);
    }

    private List<CategoryModel> getAllCategoryByIds(List<Long> categoryIds) {
        Set<Long> ids = new HashSet<>(categoryIds);
        List<CategoryModel> categories = categoryService.getAllEntitiesById(ids);
        if(categories.size() != ids.size()) {
            throw new ResourceNotFoundException("Una o mas categorías no existen");
        }
        return categories;
    }

    private List<BookCategoryModel> buildBookCategories(
        BookModel book,
        List<CategoryModel> categories
    ) {
        return categories.stream().map(category -> {
            BookCategoryModel bc = new BookCategoryModel();
            bc.setBook(book);
            bc.setCategory(category);
            return bc;}
        ).toList();
    }
}
