package com.retailshop.controller;

import com.retailshop.dto.response.ApiResponse;
import com.retailshop.dto.response.DashboardResponse;
import com.retailshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IProductService productService;
    private final ICustomerService customerService;
    private final ISupplierService supplierService;
    private final IInventoryService inventoryService;
    private final IPurchaseOrderService purchaseOrderService;
    private final ISalesOrderService salesOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardData() {
        DashboardResponse dashboard = DashboardResponse.builder()
                .totalProducts((long) productService.getAllProducts().size())
                .totalCustomers((long) customerService.getAllCustomers().size())
                .totalSuppliers((long) supplierService.getAllSuppliers().size())
                .lowStockItems((long) inventoryService.getLowStockItems().size())
                .outOfStockItems((long) inventoryService.getOutOfStockItems().size())
                .pendingPurchaseOrders((long) purchaseOrderService.getPendingOrders().size())
                .pendingSalesOrders((long) salesOrderService.getPendingOrders().size())
                .totalSalesToday(BigDecimal.ZERO) // Calculate from transactions
                .totalSalesThisMonth(BigDecimal.ZERO) // Calculate from transactions
                .totalInventoryValue(calculateInventoryValue())
                .recentTransactions(new HashMap<>())
                .topSellingProducts(new HashMap<>())
                .build();

        ApiResponse<DashboardResponse> response = ApiResponse.<DashboardResponse>builder()
                .success(true)
                .message("Dashboard data retrieved successfully")
                .data(dashboard)
                .build();
        return ResponseEntity.ok(response);
    }

    private BigDecimal calculateInventoryValue() {
        return inventoryService.getAllInventory().stream()
                .map(inv -> {
                    var product = productService.getProductById(inv.getProductId());
                    return product.getCostPrice().multiply(new BigDecimal(inv.getQuantityOnHand()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
