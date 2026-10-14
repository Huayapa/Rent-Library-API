package com.huayapadev.rentlibraryapi.loan.model;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.loan.model.enums.LoanStatusEnum;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;

@Entity(name = "Loan")
@Table(name = "loans")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor
@Getter
@Setter
public class LoanModel {
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
    @Column(name = "loaned_at", nullable = false, updatable = false)
    private Instant loanedAt;

    @Column(name = "due_at", nullable = false)
    private Instant dueAt;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ColumnDefault("ACTIVE")
    private LoanStatusEnum status = LoanStatusEnum.ACTIVE;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
