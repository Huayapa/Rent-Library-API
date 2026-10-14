package com.huayapadev.rentlibraryapi.book;

import com.huayapadev.rentlibraryapi.book.model.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<BookModel, Long>, JpaSpecificationExecutor<BookModel> {
    @Query(value = "SELECT * FROM books WHERE isbn = :isbn", nativeQuery = true)
    Optional<BookModel> findByIsbnIgnoringSoftDelete(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM books WHERE id = :id", nativeQuery = true)
    Optional<BookModel> findByIdIgnoringSoftDelete(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE books SET deleted_at = now() WHERE id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Long id);
}
