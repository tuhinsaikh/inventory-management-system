package com.retailshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalProducts;
    private Long totalCustomers;
    private Long totalSuppliers;
    private Long lowStockItems;
    private Long outOfStockItems;
    private Long pendingPurchaseOrders;
    private Long pendingSalesOrders;
    private BigDecimal totalSalesToday;
    private BigDecimal totalSalesThisMonth;
    private BigDecimal totalInventoryValue;
    private Map<String, Object> recentTransactions;
    private Map<String, Object> topSellingProducts;
}
