package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.CategoryRequest;
import com.revshop.entity.Category;
import com.revshop.mapper.CategoryMapper;
import com.revshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public ApiResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = CategoryMapper.toNewEntity(request);
        categoryRepository.save(category);

        return new ApiResponse(true, "Category created");
    }

    @Transactional
    public ApiResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        CategoryMapper.applyUpdate(category, request);
        categoryRepository.save(category);

        return new ApiResponse(true, "Category updated");
    }

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAll().stream()
                .filter(Category::isActive)
                .toList();
    }
}
