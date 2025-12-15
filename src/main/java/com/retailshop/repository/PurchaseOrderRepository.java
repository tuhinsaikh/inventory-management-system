package com.retailshop.repository;

import com.retailshop.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    List<PurchaseOrder> findBySupplier_SupplierId(Long supplierId);
    List<PurchaseOrder> findByStatus(PurchaseOrder.OrderStatus status);
    List<PurchaseOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = 'SUBMITTED' OR po.status = 'APPROVED'")
    List<PurchaseOrder> findPendingOrders();

    Boolean existsByPoNumber(String poNumber);
}
