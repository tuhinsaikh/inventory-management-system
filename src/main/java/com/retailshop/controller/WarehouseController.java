package com.retailshop.controller;

import com.retailshop.dto.response.ApiResponse;
import com.retailshop.entity.Warehouse;
import com.retailshop.service.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final IWarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Warehouse>>> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        ApiResponse<List<Warehouse>> response = ApiResponse.<List<Warehouse>>builder()
                .success(true)
                .message("Warehouses retrieved successfully")
                .data(warehouses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Warehouse>>> getActiveWarehouses() {
        List<Warehouse> warehouses = warehouseService.getActiveWarehouses();
        ApiResponse<List<Warehouse>> response = ApiResponse.<List<Warehouse>>builder()
                .success(true)
                .message("Active warehouses retrieved successfully")
                .data(warehouses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<ApiResponse<Warehouse>> getWarehouseById(@PathVariable Long warehouseId) {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        ApiResponse<Warehouse> response = ApiResponse.<Warehouse>builder()
                .success(true)
                .message("Warehouse retrieved successfully")
                .data(warehouse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Warehouse>> createWarehouse(@RequestBody Warehouse warehouse) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(warehouse);
        ApiResponse<Warehouse> response = ApiResponse.<Warehouse>builder()
                .success(true)
                .message("Warehouse created successfully")
                .data(createdWarehouse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Warehouse>> updateWarehouse(
            @PathVariable Long warehouseId,
            @RequestBody Warehouse warehouse) {
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouseId, warehouse);
        ApiResponse<Warehouse> response = ApiResponse.<Warehouse>builder()
                .success(true)
                .message("Warehouse updated successfully")
                .data(updatedWarehouse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteWarehouse(@PathVariable Long warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Warehouse deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
