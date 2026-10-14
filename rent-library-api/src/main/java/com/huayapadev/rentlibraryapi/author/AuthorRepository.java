package com.huayapadev.rentlibraryapi.author;

import com.huayapadev.rentlibraryapi.author.model.AuthorModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorModel, Long> {
    Page<AuthorModel> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
