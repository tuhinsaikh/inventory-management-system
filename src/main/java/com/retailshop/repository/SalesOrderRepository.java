package com.retailshop.repository;

import com.retailshop.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findBySoNumber(String soNumber);
    List<SalesOrder> findByCustomer_CustomerId(Long customerId);
    List<SalesOrder> findByStatus(SalesOrder.OrderStatus status);
    List<SalesOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<SalesOrder> findByPaymentStatus(SalesOrder.PaymentStatus paymentStatus);

    @Query("SELECT so FROM SalesOrder so WHERE so.status = 'PENDING' OR so.status = 'CONFIRMED'")
    List<SalesOrder> findPendingOrders();

    Boolean existsBySoNumber(String soNumber);
}
