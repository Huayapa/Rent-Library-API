package com.huayapadev.rentlibraryapi.loan;

import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanModel, Long>, JpaSpecificationExecutor<LoanModel> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Loan l SET l.status = 'OVERDUE' WHERE l.status = 'ACTIVE' AND l.returnedAt IS NULL AND l.dueAt < :now")
    void markOverdueLoans(@Param("now") Instant now);

    @Query(value = "SELECT * FROM loans WHERE id = :id", nativeQuery = true)
    Optional<LoanModel> findByIdIgnoringSoftDelete(@Param("id") Long id);

    Optional<LoanModel> findByIdAndUserId(Long id, Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE loans SET deleted_at = now() WHERE id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Long id);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND (l.status = 'ACTIVE' OR l.status = 'OVERDUE')")
    long countActiveOrOverdueByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM loans WHERE user_id = :userId AND status = 'ACTIVE')", nativeQuery = true)
    boolean existsByUserIdAndStatusActive(@Param("userId") Long userId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM loans WHERE copy_id = :copyId)", nativeQuery = true)
    boolean existsByCopyIdIgnoringSoftDelete(@Param("copyId") Long copyId);
}
