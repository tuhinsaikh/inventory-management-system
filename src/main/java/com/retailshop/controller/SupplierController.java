package com.retailshop.controller;

import com.retailshop.dto.response.ApiResponse;
import com.retailshop.entity.Supplier;
import com.retailshop.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Supplier>>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        ApiResponse<List<Supplier>> response = ApiResponse.<List<Supplier>>builder()
                .success(true)
                .message("Suppliers retrieved successfully")
                .data(suppliers)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<ApiResponse<Supplier>> getSupplierById(@PathVariable Long supplierId) {
        Supplier supplier = supplierService.getSupplierById(supplierId);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .success(true)
                .message("Supplier retrieved successfully")
                .data(supplier)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(@RequestBody Supplier supplier) {
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .success(true)
                .message("Supplier created successfully")
                .data(createdSupplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<ApiResponse<Supplier>> updateSupplier(
            @PathVariable Long supplierId,
            @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.updateSupplier(supplierId, supplier);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .success(true)
                .message("Supplier updated successfully")
                .data(updatedSupplier)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<ApiResponse<String>> deleteSupplier(@PathVariable Long supplierId) {
        supplierService.deleteSupplier(supplierId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Supplier deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
