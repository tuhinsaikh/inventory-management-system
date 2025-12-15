package com.retailshop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderRequest {
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    private LocalDate expectedDeliveryDate;
    private String notes;

    @NotEmpty(message = "Purchase order must have at least one item")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}
