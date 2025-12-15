package com.retailshop.repository;

import com.retailshop.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByProduct_ProductId(Long productId);
    List<InventoryTransaction> findByWarehouse_WarehouseId(Long warehouseId);
    List<InventoryTransaction> findByTransactionType(InventoryTransaction.TransactionType transactionType);
    List<InventoryTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.productId = :productId AND it.warehouse.warehouseId = :warehouseId ORDER BY it.transactionDate DESC")
    List<InventoryTransaction> findTransactionHistory(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);
}
