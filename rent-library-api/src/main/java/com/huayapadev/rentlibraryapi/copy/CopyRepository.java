package com.huayapadev.rentlibraryapi.copy;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CopyRepository extends JpaRepository<CopyModel, Long>, JpaSpecificationExecutor<CopyModel> {
    @Query(value = "SELECT * FROM copies WHERE inventory_code = :inventoryCode", nativeQuery = true)
    Optional<CopyModel> findByInventoryCodeIgnoringSoftDelete(@Param("inventoryCode") String inventoryCode);

    @Query(value = "SELECT * FROM copies WHERE id = :id", nativeQuery = true)
    Optional<CopyModel> findByIdIgnoringSoftDelete(@Param("id") Long id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM copies WHERE id != :id AND inventory_code = :inventoryCode)", nativeQuery = true)
    Boolean existsByInventoryCodeAndIdNot(@Param("inventoryCode") String inventoryCode, @Param("id") Long id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM copies WHERE id = :id)", nativeQuery = true)
    Boolean existsByIdIgnoringSoftDelete(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE copies SET deleted_at = now() WHERE id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        UPDATE copies c SET deleted_at = now()
        WHERE c.book_id = :bookId
        AND c.status IN('AVAILABLE', 'DAMAGED', 'LOST')
        AND c.deleted_at IS NULL
        """, nativeQuery = true)
    void softDeleteAvailableDamagedOrLostByBookId(@Param("bookId") Long bookId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        UPDATE copies c SET deleted_at = null
        WHERE c.book_id = :bookId
        AND c.status IN('AVAILABLE', 'DAMAGED', 'LOST')
        AND c.deleted_at IS NOT NULL
        """, nativeQuery = true)
    void restoreAvailableDamagedOrLostByBookId(@Param("bookId") Long bookId);
}
