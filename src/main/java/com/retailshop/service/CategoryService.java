package com.retailshop.service;

import com.retailshop.entity.Category;
import com.retailshop.exception.ResourceNotFoundException;
import com.retailshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, Category category) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setCategoryName(category.getCategoryName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setParentCategory(category.getParentCategory());
        existingCategory.setIsActive(category.getIsActive());
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getParentCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParentCategory_CategoryId(parentId);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }
}
