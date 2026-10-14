package com.huayapadev.rentlibraryapi.category.model;

import com.huayapadev.rentlibraryapi.bookcategory.model.BookCategoryModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity(name = "Category")
@Table(name = "categories")
@NoArgsConstructor
@Getter
@Setter
public class CategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<BookCategoryModel> bookCategories;
}