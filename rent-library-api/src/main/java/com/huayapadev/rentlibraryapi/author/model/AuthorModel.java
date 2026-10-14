package com.huayapadev.rentlibraryapi.author.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity(name = "Author")
@Table(name = "authors")
@NoArgsConstructor
@Getter
@Setter
public class AuthorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @ColumnDefault("'Desconocido'")
    private String name = "Desconocido";

    @Column(columnDefinition = "TEXT", length = 500)
    private String bio;
}
