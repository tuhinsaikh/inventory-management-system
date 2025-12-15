package com.retailshop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SalesOrderRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    private LocalDate deliveryDate;
    private String paymentMethod;
    private BigDecimal discountAmount;
    private String notes;

    @NotEmpty(message = "Sales order must have at least one item")
    @Valid
    private List<SalesOrderItemRequest> items;
}
