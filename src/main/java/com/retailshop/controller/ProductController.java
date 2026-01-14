package com.retailshop.controller;

import com.retailshop.dto.request.ProductRequest;
import com.retailshop.dto.response.ApiResponse;
import com.retailshop.dto.response.ProductResponse;
import com.retailshop.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts() {
        List<ProductResponse> products = productService.getActiveProducts();
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Active products retrieved successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long productId) {
        ProductResponse product = productService.getProductById(productId);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(@RequestParam String keyword) {
        List<ProductResponse> products = productService.searchProducts(keyword);
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Search completed successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product created successfully")
                .data(product)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(productId, request);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product updated successfully")
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Product deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}/deactivate")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> deactivateProduct(@PathVariable Long productId) {
        productService.deactivateProduct(productId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Product deactivated successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
