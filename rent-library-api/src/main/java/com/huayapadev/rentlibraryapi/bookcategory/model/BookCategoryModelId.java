package com.huayapadev.rentlibraryapi.bookcategory.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookCategoryModelId implements Serializable {
    private Long bookId;
    private Long categoryId;
}
