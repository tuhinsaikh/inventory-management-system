package com.retailshop.repository;

import com.retailshop.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct_ProductIdAndWarehouse_WarehouseId(Long productId, Long warehouseId);
    List<Inventory> findByProduct_ProductId(Long productId);
    List<Inventory> findByWarehouse_WarehouseId(Long warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.quantityOnHand <= i.product.reorderPoint")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.quantityOnHand = 0")
    List<Inventory> findOutOfStockItems();

    @Query("SELECT SUM(i.quantityOnHand) FROM Inventory i WHERE i.product.productId = :productId")
    Integer getTotalStockByProduct(@Param("productId") Long productId);
}
