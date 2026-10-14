package com.huayapadev.rentlibraryapi.publisher.service;

import com.huayapadev.rentlibraryapi.publisher.PublisherMapper;
import com.huayapadev.rentlibraryapi.publisher.model.PublisherModel;
import com.huayapadev.rentlibraryapi.publisher.PublisherRepository;
import com.huayapadev.rentlibraryapi.publisher.dto.PublisherRequestDTO;
import com.huayapadev.rentlibraryapi.publisher.dto.PublisherResponseDTO;
import com.huayapadev.rentlibraryapi.shared.exception.AlreadyExistsException;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    @Transactional
    public List<PublisherResponseDTO> getAll(String name, Pageable pageable) {
        Page<PublisherModel> publishers = (name != null && !name.isBlank())
            ? publisherRepository.findByNameContainingIgnoreCase(name, pageable)
            : publisherRepository.findAll(pageable);
        return publishers.stream()
            .map(publisherMapper::toResponseDTO)
            .toList();
    }

    @Transactional
    public PublisherResponseDTO getById(Long id) {
        PublisherModel publisher = getEntityById(id);
        return publisherMapper.toResponseDTO(publisher);
    }

    @Transactional
    public PublisherResponseDTO create(PublisherRequestDTO request) {
        if(publisherRepository.existsByNameIgnoreCase(request.name())) {
            throw new AlreadyExistsException("una editorial", "el nombre", request.name());
        }
        PublisherModel publisher = new PublisherModel();
        publisher.setName(request.name());
        PublisherModel savedPublisher = publisherRepository.save(publisher);
        return publisherMapper.toResponseDTO(savedPublisher);
    }

    @Transactional
    public PublisherResponseDTO put(Long id, PublisherRequestDTO request) {
        PublisherModel publisher = getEntityById(id);

        if(publisherRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new AlreadyExistsException("un editor", "el nombre", request.name());
        }

        publisher.setName(request.name());
        PublisherModel savedPublisher = publisherRepository.save(publisher);
        return publisherMapper.toResponseDTO(savedPublisher);
    }

    @Transactional
    public void delete(Long id) {
        PublisherModel publisher = getEntityById(id);
        publisherRepository.delete(publisher);
    }

    public PublisherModel getEntityById(Long id) {
        return publisherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Publisher no encontrado con el id " + id));
    }
}
