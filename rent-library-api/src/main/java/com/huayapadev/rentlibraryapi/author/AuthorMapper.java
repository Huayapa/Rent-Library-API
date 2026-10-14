package com.huayapadev.rentlibraryapi.author;

import com.huayapadev.rentlibraryapi.author.dto.AuthorResponseDTO;
import com.huayapadev.rentlibraryapi.author.model.AuthorModel;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
    public AuthorResponseDTO toResponseDTO(AuthorModel author) {
        return new AuthorResponseDTO(
            author.getId(),
            author.getName(),
            author.getBio()
        );
    }
}
