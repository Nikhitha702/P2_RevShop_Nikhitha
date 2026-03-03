package com.revshop.mapper;

import com.revshop.dto.CategoryRequest;
import com.revshop.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toNewEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setActive(true);
        return category;
    }

    public static void applyUpdate(Category category, CategoryRequest request) {
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
    }
}
