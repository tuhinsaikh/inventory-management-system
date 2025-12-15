package com.retailshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String sku;
    private String barcode;
    private String productName;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String unitOfMeasure;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private Integer reorderQuantity;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
