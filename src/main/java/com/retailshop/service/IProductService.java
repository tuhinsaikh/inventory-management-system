package com.retailshop.service;

import com.retailshop.dto.request.ProductRequest;
import com.retailshop.dto.response.ProductResponse;
import com.retailshop.entity.Product;

import java.util.List;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long productId, ProductRequest request);
    ProductResponse getProductById(Long productId);
    Product getProductEntityById(Long productId);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getActiveProducts();
    List<ProductResponse> getProductsByCategory(Long categoryId);
    List<ProductResponse> searchProducts(String keyword);
    void deleteProduct(Long productId);
    void deactivateProduct(Long productId);
}
