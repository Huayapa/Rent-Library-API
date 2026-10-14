package com.huayapadev.rentlibraryapi.author.service;

import com.huayapadev.rentlibraryapi.author.AuthorMapper;
import com.huayapadev.rentlibraryapi.author.model.AuthorModel;
import com.huayapadev.rentlibraryapi.author.AuthorRepository;
import com.huayapadev.rentlibraryapi.author.dto.AuthorPatchRequestDTO;
import com.huayapadev.rentlibraryapi.author.dto.AuthorRequestDTO;
import com.huayapadev.rentlibraryapi.author.dto.AuthorResponseDTO;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Transactional
    public List<AuthorResponseDTO> getAll(String name, Pageable pageable) {
        Page<AuthorModel> authors = (name != null && !name.isBlank())
            ? authorRepository.findByNameContainingIgnoreCase(name, pageable)
            : authorRepository.findAll(pageable);
        return authors.stream()
            .map(authorMapper::toResponseDTO)
            .toList();
    }

    @Transactional
    public AuthorResponseDTO getById(Long id) {
        AuthorModel author = getEntityById(id);
        return authorMapper.toResponseDTO(author);
    }

    @Transactional
    public AuthorResponseDTO create(AuthorRequestDTO request) {
        AuthorModel author = new AuthorModel();
        author.setName(request.name());
        author.setBio(request.bio());

        AuthorModel savedAuthor = authorRepository.save(author);
        return authorMapper.toResponseDTO(savedAuthor);
    }

    @Transactional
    public AuthorResponseDTO patch(Long id, AuthorPatchRequestDTO request) {
        AuthorModel author = getEntityById(id);
        if(request.name().isPresent()) {
            author.setName(request.name().get());
        }
        if(request.bio().isPresent()) {
            author.setBio(request.bio().get());
        }
        AuthorModel savedAuthor = authorRepository.save(author);
        return authorMapper.toResponseDTO(savedAuthor);
    }

    @Transactional
    public void delete(Long id) {
        AuthorModel author = getEntityById(id);
        authorRepository.delete(author);
    }

    public AuthorModel getEntityById(Long id) {
        return authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con id: " + id));
    }
}
