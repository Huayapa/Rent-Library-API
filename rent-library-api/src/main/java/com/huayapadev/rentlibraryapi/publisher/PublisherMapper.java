package com.huayapadev.rentlibraryapi.publisher;

import com.huayapadev.rentlibraryapi.publisher.dto.PublisherResponseDTO;
import com.huayapadev.rentlibraryapi.publisher.model.PublisherModel;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {
    public PublisherResponseDTO toResponseDTO(PublisherModel publisher) {
        return new PublisherResponseDTO(
            publisher.getId(),
            publisher.getName()
        );
    }
}
