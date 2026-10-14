package com.huayapadev.rentlibraryapi.copy.dto;

import com.huayapadev.rentlibraryapi.copy.model.enums.CopyStatusEnum;

public record CopyFilterDTO(
    String inventoryCode,
    Long bookId,
    CopyStatusEnum status
) {
}
