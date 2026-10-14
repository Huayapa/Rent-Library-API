package com.huayapadev.rentlibraryapi.copy.service;

import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.book.service.BookService;
import com.huayapadev.rentlibraryapi.copy.CopyMapper;
import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.copy.dto.CopyRequestDTO;
import com.huayapadev.rentlibraryapi.copy.dto.CopyResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CopyCreateService {
    private final CopyService copyService;
    private final BookService bookService;
    private final CopyMapper copyMapper;

    @Transactional
    public CopyResponseDTO create(CopyRequestDTO request) {
        copyService.verifyInventoryCodeNotUse(request.inventoryCode());
        BookModel book = bookService.getEntityById(request.bookId());
        CopyModel savedCopy = copyService.create(request.inventoryCode(), book);
        return copyMapper.toResponseDTO(savedCopy);
    }
}
