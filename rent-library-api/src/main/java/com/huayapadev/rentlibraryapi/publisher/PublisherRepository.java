package com.huayapadev.rentlibraryapi.publisher;

import com.huayapadev.rentlibraryapi.publisher.model.PublisherModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<PublisherModel, Long> {
    @Query("SELECT p FROM Publisher p WHERE p.name LIKE LOWER(CONCAT('%',:name,'%'))")
    Page<PublisherModel> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    // NOTE: Usar pageable para limitar solo a 1
    @Query("SELECT p FROM Publisher p WHERE lower(p.name) = lower(:name)")
    Optional<Page<PublisherModel>> findByNameIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM publishers WHERE lower(name) = lower(:name))", nativeQuery = true)
    Boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM publishers WHERE lower(name) = lower(:name) AND id != :id)", nativeQuery = true)
    Boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
}
