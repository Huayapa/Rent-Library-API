package com.huayapadev.rentlibraryapi.copy;

import com.huayapadev.rentlibraryapi.book.BookMapper;
import com.huayapadev.rentlibraryapi.copy.dto.CopyResponseDTO;
import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CopyMapper {
    private final BookMapper bookmapper;
    public CopyResponseDTO toResponseDTO(CopyModel copy) {
        return new CopyResponseDTO(
            copy.getId(),
            copy.getInventoryCode(),
            bookmapper.toResponseDTO(copy.getBook()),
            copy.getStatus()
        );
    }
}
