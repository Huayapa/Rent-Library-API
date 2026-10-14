package com.huayapadev.rentlibraryapi.reservation;

import com.huayapadev.rentlibraryapi.reservation.model.ReservationModel;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class ReservationSpecification {
    public static Specification<ReservationModel> hasUserId(Long userId) {
        return (root, query, cb)
            -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<ReservationModel> hasStatus(ReservationStatusEnum status) {
        return (root, query, cb)
            -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<ReservationModel> reservedAfter(Instant from) {
        return (root, query, cb)
            -> from == null ? null : cb.greaterThanOrEqualTo(root.get("reservedAt"), from);
    }

    public static Specification<ReservationModel> reservedBefore(Instant to) {
        return (root, query, cb)
            -> to == null ? null : cb.lessThanOrEqualTo(root.get("reservedAt"), to);
    }

    public static Specification<ReservationModel> expiresAfter(Instant from) {
        return (root, query, cb)
            -> from == null ? null : cb.greaterThanOrEqualTo(root.get("expiresAt"), from);
    }

    public static Specification<ReservationModel> expiresBefore(Instant to) {
        return (root, query, cb)
            -> to == null ? null : cb.lessThanOrEqualTo(root.get("expiresAt"), to);
    }
}
