package com.retailshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {
    private Long poId;
    private String poNumber;
    private Long supplierId;
    private String supplierName;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private String notes;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
