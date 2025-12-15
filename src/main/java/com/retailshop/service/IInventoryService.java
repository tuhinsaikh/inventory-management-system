package com.retailshop.service;

import com.retailshop.dto.request.InventoryUpdateRequest;
import com.retailshop.dto.response.InventoryResponse;
import com.retailshop.entity.Inventory;

import java.util.List;

public interface IInventoryService {
    InventoryResponse getInventory(Long productId, Long warehouseId);
    List<InventoryResponse> getInventoryByProduct(Long productId);
    List<InventoryResponse> getInventoryByWarehouse(Long warehouseId);
    List<InventoryResponse> getAllInventory();
    List<InventoryResponse> getLowStockItems();
    List<InventoryResponse> getOutOfStockItems();
    InventoryResponse updateInventory(InventoryUpdateRequest request);
    InventoryResponse adjustInventory(Long productId, Long warehouseId, Integer quantity, String reason);
    void transferStock(Long productId, Long fromWarehouseId, Long toWarehouseId, Integer quantity);
    Integer getTotalStockByProduct(Long productId);
    boolean checkStockAvailability(Long productId, Long warehouseId, Integer requiredQuantity);
    void reserveStock(Long productId, Long warehouseId, Integer quantity);
    void releaseReservedStock(Long productId, Long warehouseId, Integer quantity);
}
