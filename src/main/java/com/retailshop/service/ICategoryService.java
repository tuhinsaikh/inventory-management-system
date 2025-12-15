package com.retailshop.service;

import com.retailshop.entity.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(Category category);
    Category updateCategory(Long categoryId, Category category);
    Category getCategoryById(Long categoryId);
    List<Category> getAllCategories();
    List<Category> getActiveCategories();
    List<Category> getParentCategories();
    List<Category> getSubCategories(Long parentId);
    void deleteCategory(Long categoryId);
}
