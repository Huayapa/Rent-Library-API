package com.huayapadev.rentlibraryapi.reservation;

import com.huayapadev.rentlibraryapi.reservation.model.ReservationModel;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationModel, Long>, JpaSpecificationExecutor<ReservationModel> {
    @Query("SELECT COUNT(*) FROM Reservation r WHERE r.user.id = :userId AND r.status = 'PENDING'")
    long countPendingByUserId(@Param("userId") Long userId);

    Optional<ReservationModel> findByIdAndUserId(Long id, Long userId);

    Optional<ReservationModel> findByCopyIdAndStatus(Long copyId, ReservationStatusEnum status);

    Optional<ReservationModel> findByIdAndExpiresAtBefore(Long reservationId, Instant expiresAt);
    List<ReservationModel> findByStatusAndExpiresAtBefore(ReservationStatusEnum status, Instant expiresAt);

    boolean existsByCopyIdAndStatus(Long copyId, ReservationStatusEnum status);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE reservations SET status =  'CANCELLED' WHERE user_id = :userId AND status = 'PENDING' AND deleted_at IS NULL", nativeQuery = true)
    void cancelByUserIdAndStatusPending(@Param("userId") Long userId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM reservations WHERE copy_id = :copyId)", nativeQuery = true)
    boolean existsByCopyIdIgnoringSoftDelete(@Param("copyId") Long copyId);
}
