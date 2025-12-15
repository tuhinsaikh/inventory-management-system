package com.retailshop.controller;

import com.retailshop.dto.request.SalesOrderRequest;
import com.retailshop.dto.response.ApiResponse;
import com.retailshop.dto.response.SalesOrderResponse;
import com.retailshop.entity.SalesOrder;
import com.retailshop.service.ISalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final ISalesOrderService salesOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getAllSalesOrders() {
        List<SalesOrderResponse> orders = salesOrderService.getAllSalesOrders();
        ApiResponse<List<SalesOrderResponse>> response = ApiResponse.<List<SalesOrderResponse>>builder()
                .success(true)
                .message("Sales orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{soId}")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> getSalesOrderById(@PathVariable Long soId) {
        SalesOrderResponse order = salesOrderService.getSalesOrderById(soId);
        ApiResponse<SalesOrderResponse> response = ApiResponse.<SalesOrderResponse>builder()
                .success(true)
                .message("Sales order retrieved successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getSalesOrdersByCustomer(@PathVariable Long customerId) {
        List<SalesOrderResponse> orders = salesOrderService.getSalesOrdersByCustomer(customerId);
        ApiResponse<List<SalesOrderResponse>> response = ApiResponse.<List<SalesOrderResponse>>builder()
                .success(true)
                .message("Sales orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getSalesOrdersByStatus(@PathVariable String status) {
        List<SalesOrderResponse> orders = salesOrderService.getSalesOrdersByStatus(SalesOrder.OrderStatus.valueOf(status));
        ApiResponse<List<SalesOrderResponse>> response = ApiResponse.<List<SalesOrderResponse>>builder()
                .success(true)
                .message("Sales orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getPendingOrders() {
        List<SalesOrderResponse> orders = salesOrderService.getPendingOrders();
        ApiResponse<List<SalesOrderResponse>> response = ApiResponse.<List<SalesOrderResponse>>builder()
                .success(true)
                .message("Pending sales orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> createSalesOrder(@Valid @RequestBody SalesOrderRequest request) {
        SalesOrderResponse order = salesOrderService.createSalesOrder(request);
        ApiResponse<SalesOrderResponse> response = ApiResponse.<SalesOrderResponse>builder()
                .success(true)
                .message("Sales order created successfully")
                .data(order)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{soId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> updateSalesOrder(
            @PathVariable Long soId,
            @Valid @RequestBody SalesOrderRequest request) {
        SalesOrderResponse order = salesOrderService.updateSalesOrder(soId, request);
        ApiResponse<SalesOrderResponse> response = ApiResponse.<SalesOrderResponse>builder()
                .success(true)
                .message("Sales order updated successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{soId}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> confirmSalesOrder(@PathVariable Long soId) {
        SalesOrderResponse order = salesOrderService.confirmSalesOrder(soId);
        ApiResponse<SalesOrderResponse> response = ApiResponse.<SalesOrderResponse>builder()
                .success(true)
                .message("Sales order confirmed successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{soId}/ship")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> shipSalesOrder(@PathVariable Long soId) {
        SalesOrderResponse order = salesOrderService.shipSalesOrder(soId);
        ApiResponse<SalesOrderResponse> response = ApiResponse.<SalesOrderResponse>builder()
                .success(true)
                .message("Sales order shipped successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{soId}/deliver")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> deliverSalesOrder(@PathVariable Long soId) {
        SalesOrderResponse order = salesOrderService.deliverSalesOrder(soId);
        ApiResponse<SalesOrderResponse> response = ApiResponse.<SalesOrderResponse>builder()
                .success(true)
                .message("Sales order delivered successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{soId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> cancelSalesOrder(@PathVariable Long soId) {
        SalesOrderResponse order = salesOrderService.cancelSalesOrder(soId);
        ApiResponse<SalesOrderResponse> response = ApiResponse.<SalesOrderResponse>builder()
                .success(true)
                .message("Sales order cancelled successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{soId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSalesOrder(@PathVariable Long soId) {
        salesOrderService.deleteSalesOrder(soId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Sales order deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
