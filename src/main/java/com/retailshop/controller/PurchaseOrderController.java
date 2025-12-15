package com.retailshop.controller;

import com.retailshop.dto.request.PurchaseOrderRequest;
import com.retailshop.dto.response.ApiResponse;
import com.retailshop.dto.response.PurchaseOrderResponse;
import com.retailshop.entity.PurchaseOrder;
import com.retailshop.service.IPurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final IPurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getAllPurchaseOrders() {
        List<PurchaseOrderResponse> orders = purchaseOrderService.getAllPurchaseOrders();
        ApiResponse<List<PurchaseOrderResponse>> response = ApiResponse.<List<PurchaseOrderResponse>>builder()
                .success(true)
                .message("Purchase orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{poId}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(@PathVariable Long poId) {
        PurchaseOrderResponse order = purchaseOrderService.getPurchaseOrderById(poId);
        ApiResponse<PurchaseOrderResponse> response = ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .message("Purchase order retrieved successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getPurchaseOrdersBySupplier(@PathVariable Long supplierId) {
        List<PurchaseOrderResponse> orders = purchaseOrderService.getPurchaseOrdersBySupplier(supplierId);
        ApiResponse<List<PurchaseOrderResponse>> response = ApiResponse.<List<PurchaseOrderResponse>>builder()
                .success(true)
                .message("Purchase orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getPurchaseOrdersByStatus(@PathVariable String status) {
        List<PurchaseOrderResponse> orders = purchaseOrderService.getPurchaseOrdersByStatus(PurchaseOrder.OrderStatus.valueOf(status));
        ApiResponse<List<PurchaseOrderResponse>> response = ApiResponse.<List<PurchaseOrderResponse>>builder()
                .success(true)
                .message("Purchase orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getPendingOrders() {
        List<PurchaseOrderResponse> orders = purchaseOrderService.getPendingOrders();
        ApiResponse<List<PurchaseOrderResponse>> response = ApiResponse.<List<PurchaseOrderResponse>>builder()
                .success(true)
                .message("Pending purchase orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderResponse order = purchaseOrderService.createPurchaseOrder(request);
        ApiResponse<PurchaseOrderResponse> response = ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .message("Purchase order created successfully")
                .data(order)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{poId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> updatePurchaseOrder(
            @PathVariable Long poId,
            @Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderResponse order = purchaseOrderService.updatePurchaseOrder(poId, request);
        ApiResponse<PurchaseOrderResponse> response = ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .message("Purchase order updated successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{poId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> approvePurchaseOrder(@PathVariable Long poId) {
        PurchaseOrderResponse order = purchaseOrderService.approvePurchaseOrder(poId);
        ApiResponse<PurchaseOrderResponse> response = ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .message("Purchase order approved successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{poId}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receivePurchaseOrder(@PathVariable Long poId) {
        PurchaseOrderResponse order = purchaseOrderService.receivePurchaseOrder(poId);
        ApiResponse<PurchaseOrderResponse> response = ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .message("Purchase order received successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{poId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> cancelPurchaseOrder(@PathVariable Long poId) {
        PurchaseOrderResponse order = purchaseOrderService.cancelPurchaseOrder(poId);
        ApiResponse<PurchaseOrderResponse> response = ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .message("Purchase order cancelled successfully")
                .data(order)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{poId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deletePurchaseOrder(@PathVariable Long poId) {
        purchaseOrderService.deletePurchaseOrder(poId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Purchase order deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
