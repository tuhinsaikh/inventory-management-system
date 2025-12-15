package com.retailshop.service;

import com.retailshop.entity.Warehouse;
import java.util.List;

public interface IWarehouseService {
    Warehouse createWarehouse(Warehouse warehouse);
    Warehouse updateWarehouse(Long warehouseId, Warehouse warehouse);
    Warehouse getWarehouseById(Long warehouseId);
    List<Warehouse> getAllWarehouses();
    List<Warehouse> getActiveWarehouses();
    void deleteWarehouse(Long warehouseId);
}
