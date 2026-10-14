package com.huayapadev.rentlibraryapi.reservation.model;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.reservation.model.enums.ReservationStatusEnum;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;

@Entity(name = "Reservation")
@Table(name = "reservations")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor
@Getter
@Setter
public class ReservationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private CopyModel copy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private UserModel user;

    @CreationTimestamp
    @Column(name = "reserved_at", nullable = false)
    private Instant reservedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ColumnDefault("PENDING")
    private ReservationStatusEnum status = ReservationStatusEnum.PENDING;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
