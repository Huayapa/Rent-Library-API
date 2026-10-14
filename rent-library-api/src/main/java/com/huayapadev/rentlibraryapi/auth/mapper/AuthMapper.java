package com.huayapadev.rentlibraryapi.auth.mapper;

import com.huayapadev.rentlibraryapi.auth.dto.AuthResultDTO;
import com.huayapadev.rentlibraryapi.auth.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMapper {
    public TokenResponseDTO toResponseDTO(AuthResultDTO result) {
        return new TokenResponseDTO(result.accessToken(), result.tokenType() ,result.expiresIn());
    }
}
