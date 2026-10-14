package com.huayapadev.rentlibraryapi.book;

import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.bookcategory.model.BookCategoryModel;
import com.huayapadev.rentlibraryapi.category.model.CategoryModel;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BookSpecifications {
    public static Specification<BookModel> hasIsbn(String isbn) {
        return (root, query, cb)
            -> isbn == null ? null : cb.equal(root.get("isbn"), isbn);
    }

    public static Specification<BookModel> hasTitle(String title) {
        return (root, query, cb)
            -> title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<BookModel> hasAuthorId(Long authorId) {
        return (root, query, cb)
            -> authorId == null ? null : cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<BookModel> hasPublisherId(Long publisherId) {
        return (root, query, cb)
            -> publisherId == null ? null : cb.equal(root.get("publisher").get("id"), publisherId);
    }

    public static Specification<BookModel> hasCategoryIds(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if(categoryIds == null || categoryIds.isEmpty()) return null;
            Join<BookModel, BookCategoryModel> bookCategoryJoin = root.join("bookCategories");
            Join<BookCategoryModel, CategoryModel> categoryJoin = bookCategoryJoin.join("category");
            query.distinct(true);
            return categoryJoin.get("id").in(categoryIds);
        };
    }
}
