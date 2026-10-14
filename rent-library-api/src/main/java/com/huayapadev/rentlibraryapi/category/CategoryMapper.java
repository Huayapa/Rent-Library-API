package com.huayapadev.rentlibraryapi.category;

import com.huayapadev.rentlibraryapi.category.dto.CategoryResponseDTO;
import com.huayapadev.rentlibraryapi.category.model.CategoryModel;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDTO toResponseDTO(CategoryModel category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
