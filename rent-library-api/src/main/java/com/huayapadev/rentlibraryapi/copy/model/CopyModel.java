package com.huayapadev.rentlibraryapi.copy.model;

import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.copy.model.enums.CopyStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Entity(name = "Copy")
@Table(name = "copies")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor
@Setter
@Getter
public class CopyModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_code", nullable = false, unique = true, length = 50)
    private String inventoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private BookModel book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ColumnDefault("AVAILABLE")
    private CopyStatusEnum status = CopyStatusEnum.AVAILABLE;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
