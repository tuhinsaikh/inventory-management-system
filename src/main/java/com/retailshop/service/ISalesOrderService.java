package com.retailshop.service;

import com.retailshop.dto.request.SalesOrderRequest;
import com.retailshop.dto.response.SalesOrderResponse;
import com.retailshop.entity.SalesOrder;

import java.util.List;

public interface ISalesOrderService {
    SalesOrderResponse createSalesOrder(SalesOrderRequest request);
    SalesOrderResponse updateSalesOrder(Long soId, SalesOrderRequest request);
    SalesOrderResponse getSalesOrderById(Long soId);
    List<SalesOrderResponse> getAllSalesOrders();
    List<SalesOrderResponse> getSalesOrdersByCustomer(Long customerId);
    List<SalesOrderResponse> getSalesOrdersByStatus(SalesOrder.OrderStatus status);
    List<SalesOrderResponse> getPendingOrders();
    SalesOrderResponse confirmSalesOrder(Long soId);
    SalesOrderResponse shipSalesOrder(Long soId);
    SalesOrderResponse deliverSalesOrder(Long soId);
    SalesOrderResponse cancelSalesOrder(Long soId);
    void deleteSalesOrder(Long soId);
}
