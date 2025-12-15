package com.retailshop.repository;

import com.retailshop.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
    List<SalesOrderItem> findBySalesOrder_SoId(Long soId);
    List<SalesOrderItem> findByProduct_ProductId(Long productId);
}
