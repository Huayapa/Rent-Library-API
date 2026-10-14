package com.huayapadev.rentlibraryapi.bookcategory.model;

import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.category.model.CategoryModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity(name = "BookCategory")
@Table(name = "book_categories")
@NoArgsConstructor
@Getter
@Setter
public class BookCategoryModel {

    @EmbeddedId
    private BookCategoryModelId id;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BookModel book;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryModel category;
}
