package com.huayapadev.rentlibraryapi.book.model;

import com.huayapadev.rentlibraryapi.author.model.AuthorModel;
import com.huayapadev.rentlibraryapi.bookcategory.model.BookCategoryModel;
import com.huayapadev.rentlibraryapi.publisher.model.PublisherModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "Book")
@Table(name = "books")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor
@Getter
@Setter
public class BookModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_data")
    private LocalDate releaseData;

    @Column(name = "is_published", nullable = false)
    @ColumnDefault("false")
    private Boolean isPublished = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private AuthorModel author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private PublisherModel publisher;

    @Column(name = "scheduled_publish_at")
    private Instant scheduledPublishAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookCategoryModel> bookCategories;
}
