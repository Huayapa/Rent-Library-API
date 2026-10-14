package com.huayapadev.rentlibraryapi.copy.dto;

import com.huayapadev.rentlibraryapi.copy.model.enums.CopyStatusEnum;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record CopyPatchRequestDTO(
    @Size(max = 50, message = "El inventoryCode no debe exceder los 50 caracteres.")
    JsonNullable<String> inventoryCode,
    JsonNullable<CopyStatusEnum> status
) {
    public CopyPatchRequestDTO {
        inventoryCode = inventoryCode == null ? JsonNullable.undefined() : inventoryCode;
        status = status == null ? JsonNullable.undefined() : status;
    }
}
