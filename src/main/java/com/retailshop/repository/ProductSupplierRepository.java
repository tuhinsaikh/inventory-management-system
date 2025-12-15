package com.retailshop.repository;

import com.retailshop.entity.ProductSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, Long> {
    List<ProductSupplier> findByProduct_ProductId(Long productId);
    List<ProductSupplier> findBySupplier_SupplierId(Long supplierId);
    List<ProductSupplier> findByProduct_ProductIdAndIsPreferredTrue(Long productId);
}
