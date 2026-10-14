package com.huayapadev.rentlibraryapi.bookcategory;

import com.huayapadev.rentlibraryapi.bookcategory.model.BookCategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookCategoryRepository extends JpaRepository<BookCategoryModel, Long> {
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.book.id = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);
}
