package com.huayapadev.rentlibraryapi.category;

import com.huayapadev.rentlibraryapi.category.model.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {
    @Query("SELECT c FROM Category c WHERE CAST(function('unaccent_immutable', LOWER(c.name)) AS String) LIKE CAST(function('unaccent_immutable', LOWER(CONCAT('%', :name, '%'))) AS String)")
    Page<CategoryModel> findByNameContainingIgnoreAccentsAndCase(@Param("name") String name, Pageable pageable);
    @Query(value = """
        SELECT EXISTS(
            SELECT 1 FROM categories
            WHERE unaccent_immutable(lower(name)) = unaccent_immutable(lower(:name))
        )
        """, nativeQuery = true)
    boolean existsByNameIgnoreAccentsAndCase(@Param("name") String name);

    @Query(value = """
        SELECT EXISTS(
            SELECT 1 FROM categories
            WHERE unaccent_immutable(lower(name)) = unaccent_immutable(lower(:name))
            AND id != :id
        )
        """, nativeQuery = true)
    boolean existsByNameIgnoreAccentsAndCaseAndIdNot(@Param("name") String name,@Param("id") Long id);
}
