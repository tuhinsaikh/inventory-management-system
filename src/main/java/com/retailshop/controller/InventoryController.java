package com.retailshop.controller;

import com.retailshop.dto.request.InventoryUpdateRequest;
import com.retailshop.dto.response.ApiResponse;
import com.retailshop.dto.response.InventoryResponse;
import com.retailshop.service.IInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllInventory() {
        List<InventoryResponse> inventory = inventoryService.getAllInventory();
        ApiResponse<List<InventoryResponse>> response = ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .message("Inventory retrieved successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryByProduct(@PathVariable Long productId) {
        List<InventoryResponse> inventory = inventoryService.getInventoryByProduct(productId);
        ApiResponse<List<InventoryResponse>> response = ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .message("Inventory retrieved successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        List<InventoryResponse> inventory = inventoryService.getInventoryByWarehouse(warehouseId);
        ApiResponse<List<InventoryResponse>> response = ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .message("Inventory retrieved successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStockItems() {
        List<InventoryResponse> inventory = inventoryService.getLowStockItems();
        ApiResponse<List<InventoryResponse>> response = ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .message("Low stock items retrieved successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getOutOfStockItems() {
        List<InventoryResponse> inventory = inventoryService.getOutOfStockItems();
        ApiResponse<List<InventoryResponse>> response = ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .message("Out of stock items retrieved successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        InventoryResponse inventory = inventoryService.getInventory(productId, warehouseId);
        ApiResponse<InventoryResponse> response = ApiResponse.<InventoryResponse>builder()
                .success(true)
                .message("Inventory retrieved successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(@Valid @RequestBody InventoryUpdateRequest request) {
        InventoryResponse inventory = inventoryService.updateInventory(request);
        ApiResponse<InventoryResponse> response = ApiResponse.<InventoryResponse>builder()
                .success(true)
                .message("Inventory updated successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjustInventory(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String reason) {
        InventoryResponse inventory = inventoryService.adjustInventory(productId, warehouseId, quantity, reason);
        ApiResponse<InventoryResponse> response = ApiResponse.<InventoryResponse>builder()
                .success(true)
                .message("Inventory adjusted successfully")
                .data(inventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> transferStock(
            @RequestParam Long productId,
            @RequestParam Long fromWarehouseId,
            @RequestParam Long toWarehouseId,
            @RequestParam Integer quantity) {
        inventoryService.transferStock(productId, fromWarehouseId, toWarehouseId, quantity);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Stock transferred successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/total")
    public ResponseEntity<ApiResponse<Integer>> getTotalStockByProduct(@PathVariable Long productId) {
        Integer total = inventoryService.getTotalStockByProduct(productId);
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .success(true)
                .message("Total stock retrieved successfully")
                .data(total)
                .build();
        return ResponseEntity.ok(response);
    }
}
