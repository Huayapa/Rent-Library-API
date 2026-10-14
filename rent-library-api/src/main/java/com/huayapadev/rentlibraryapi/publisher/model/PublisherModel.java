package com.huayapadev.rentlibraryapi.publisher.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Publisher")
@Table(name = "publishers")
@NoArgsConstructor
@Getter
@Setter
public class PublisherModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
