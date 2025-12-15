package com.retailshop.repository;

import com.retailshop.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findByPurchaseOrder_PoId(Long poId);
    List<PurchaseOrderItem> findByProduct_ProductId(Long productId);
}
