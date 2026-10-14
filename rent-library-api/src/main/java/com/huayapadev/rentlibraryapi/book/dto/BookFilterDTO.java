package com.huayapadev.rentlibraryapi.book.dto;

import java.util.List;

public record BookFilterDTO(
    String isbn,
    String title,
    Long authorId,
    Long publisherId,
    List<Long> categoryIds
) {
}
