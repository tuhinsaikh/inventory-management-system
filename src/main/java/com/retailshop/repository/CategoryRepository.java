package com.retailshop.repository;

import com.retailshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrue();
    Optional<Category> findByCategoryName(String categoryName);
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategory_CategoryId(Long parentId);
}
