package com.huayapadev.rentlibraryapi.auth.mapper;

import com.huayapadev.rentlibraryapi.auth.dto.SessionResponseDTO;
import com.huayapadev.rentlibraryapi.auth.model.TokenModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionMapper {
    public SessionResponseDTO toModel(TokenModel token, String currentTokenId) {
        return new SessionResponseDTO(
            token.getTokenId(),
            token.getDeviceInfo(),
            token.getIssuedAt(),
            token.getTokenId().equals(currentTokenId)
        );
    }
}
