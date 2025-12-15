package com.retailshop.service;

import com.retailshop.dto.request.PurchaseOrderRequest;
import com.retailshop.dto.response.PurchaseOrderResponse;
import com.retailshop.entity.PurchaseOrder;

import java.time.LocalDate;
import java.util.List;

public interface IPurchaseOrderService {
    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);
    PurchaseOrderResponse updatePurchaseOrder(Long poId, PurchaseOrderRequest request);
    PurchaseOrderResponse getPurchaseOrderById(Long poId);
    List<PurchaseOrderResponse> getAllPurchaseOrders();
    List<PurchaseOrderResponse> getPurchaseOrdersBySupplier(Long supplierId);
    List<PurchaseOrderResponse> getPurchaseOrdersByStatus(PurchaseOrder.OrderStatus status);
    List<PurchaseOrderResponse> getPendingOrders();
    PurchaseOrderResponse approvePurchaseOrder(Long poId);
    PurchaseOrderResponse receivePurchaseOrder(Long poId);
    PurchaseOrderResponse cancelPurchaseOrder(Long poId);
    void deletePurchaseOrder(Long poId);
}
