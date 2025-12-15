package com.retailshop.controller;

import com.retailshop.dto.response.ApiResponse;
import com.retailshop.entity.Category;
import com.retailshop.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        ApiResponse<List<Category>> response = ApiResponse.<List<Category>>builder()
                .success(true)
                .message("Categories retrieved successfully")
                .data(categories)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Category>>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        ApiResponse<List<Category>> response = ApiResponse.<List<Category>>builder()
                .success(true)
                .message("Active categories retrieved successfully")
                .data(categories)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parent")
    public ResponseEntity<ApiResponse<List<Category>>> getParentCategories() {
        List<Category> categories = categoryService.getParentCategories();
        ApiResponse<List<Category>> response = ApiResponse.<List<Category>>builder()
                .success(true)
                .message("Parent categories retrieved successfully")
                .data(categories)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        ApiResponse<Category> response = ApiResponse.<Category>builder()
                .success(true)
                .message("Category retrieved successfully")
                .data(category)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<ApiResponse<List<Category>>> getSubCategories(@PathVariable Long categoryId) {
        List<Category> subCategories = categoryService.getSubCategories(categoryId);
        ApiResponse<List<Category>> response = ApiResponse.<List<Category>>builder()
                .success(true)
                .message("Subcategories retrieved successfully")
                .data(subCategories)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        ApiResponse<Category> response = ApiResponse.<Category>builder()
                .success(true)
                .message("Category created successfully")
                .data(createdCategory)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(categoryId, category);
        ApiResponse<Category> response = ApiResponse.<Category>builder()
                .success(true)
                .message("Category updated successfully")
                .data(updatedCategory)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Category deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
