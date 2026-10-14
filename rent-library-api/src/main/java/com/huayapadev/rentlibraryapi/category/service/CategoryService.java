package com.huayapadev.rentlibraryapi.category.service;

import com.huayapadev.rentlibraryapi.category.CategoryMapper;
import com.huayapadev.rentlibraryapi.category.model.CategoryModel;
import com.huayapadev.rentlibraryapi.category.CategoryRepository;
import com.huayapadev.rentlibraryapi.category.dto.CategoryRequestDTO;
import com.huayapadev.rentlibraryapi.category.dto.CategoryResponseDTO;
import com.huayapadev.rentlibraryapi.shared.exception.AlreadyExistsException;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponseDTO> getAll(String name, Pageable pageable) {
        Page<CategoryModel> categories = (name != null && !name.isBlank())
            ? categoryRepository.findByNameContainingIgnoreAccentsAndCase(name, pageable)
            : categoryRepository.findAll(pageable);
        return categories.stream()
            .map(categoryMapper::toResponseDTO)
            .toList();
    }

    public List<CategoryModel> getAllEntitiesById(Collection<Long> ids) {
        if(ids == null || ids.isEmpty()) return List.of();
        return categoryRepository.findAllById(ids);
    }

    public CategoryResponseDTO getById(Long id) {
        CategoryModel category = getEntityById(id);
        return categoryMapper.toResponseDTO(category);
    }

    public CategoryModel getEntityById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        if(categoryRepository.existsByNameIgnoreAccentsAndCase(request.name())) {
            throw new AlreadyExistsException("una categoria", "el nombre", request.name(), "(o una variación similar en mayúsculas/acentos)");
        }
        CategoryModel category = new CategoryModel();
        category.setName(request.name());

        CategoryModel savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO put(Long id, CategoryRequestDTO request) {
        CategoryModel category = getEntityById(id);

        if(categoryRepository.existsByNameIgnoreAccentsAndCaseAndIdNot(request.name(), id)) {
            throw new AlreadyExistsException("una categoría", "el nombre", request.name(), "(o una variación similar en mayúsculas/acentos)");
        }

        category.setName(request.name());
        CategoryModel savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(savedCategory);
    }

    @Transactional
    public void delete(Long id) {
        CategoryModel category = getEntityById(id);
        categoryRepository.delete(category);
    }
}
