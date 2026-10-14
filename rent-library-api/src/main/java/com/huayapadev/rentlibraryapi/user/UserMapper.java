package com.huayapadev.rentlibraryapi.user;

import com.huayapadev.rentlibraryapi.user.dto.UserResponseDTO;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toResponseDTO(UserModel user) {
        return new UserResponseDTO(
            user.getId(),
            user.getDni(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
