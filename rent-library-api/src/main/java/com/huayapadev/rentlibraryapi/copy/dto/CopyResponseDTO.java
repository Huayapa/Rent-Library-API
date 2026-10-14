package com.huayapadev.rentlibraryapi.copy.dto;

import com.huayapadev.rentlibraryapi.book.dto.BookResponseDTO;
import com.huayapadev.rentlibraryapi.copy.model.enums.CopyStatusEnum;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "copies", itemRelation = "copy")
public record CopyResponseDTO(
    Long id,
    String inventoryCode,
    BookResponseDTO book,
    CopyStatusEnum status
) {
}
