package com.retailshop.service;

import com.retailshop.dto.request.PurchaseOrderItemRequest;
import com.retailshop.dto.request.PurchaseOrderRequest;
import com.retailshop.dto.response.PurchaseOrderResponse;
import com.retailshop.entity.*;
import com.retailshop.exception.ResourceNotFoundException;
import com.retailshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService implements IPurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        User currentUser = getCurrentUser();

        // Generate PO number
        String poNumber = generatePONumber();

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .poNumber(poNumber)
                .supplier(supplier)
                .warehouse(warehouse)
                .orderDate(LocalDate.now())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .status(PurchaseOrder.OrderStatus.DRAFT)
                .notes(request.getNotes())
                .createdBy(currentUser)
                .items(new ArrayList<>())
                .build();

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        // Add items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .product(product)
                    .quantityOrdered(itemRequest.getQuantity())
                    .quantityReceived(0)
                    .unitPrice(itemRequest.getUnitPrice())
                    .taxRate(itemRequest.getTaxRate() != null ? itemRequest.getTaxRate() : BigDecimal.ZERO)
                    .discountAmount(itemRequest.getDiscountAmount() != null ? itemRequest.getDiscountAmount() : BigDecimal.ZERO)
                    .build();

            purchaseOrder.getItems().add(item);
            totalAmount = totalAmount.add(item.getLineTotal());
        }

        purchaseOrder.setTotalAmount(totalAmount);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return convertToResponse(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(Long poId, PurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != PurchaseOrder.OrderStatus.DRAFT) {
            throw new IllegalStateException("Can only update purchase orders in DRAFT status");
        }

        purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        purchaseOrder.setNotes(request.getNotes());

        // Clear and update items
        purchaseOrder.getItems().clear();
        purchaseOrderItemRepository.deleteAll(purchaseOrderItemRepository.findByPurchaseOrder_PoId(poId));

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .product(product)
                    .quantityOrdered(itemRequest.getQuantity())
                    .quantityReceived(0)
                    .unitPrice(itemRequest.getUnitPrice())
                    .taxRate(itemRequest.getTaxRate() != null ? itemRequest.getTaxRate() : BigDecimal.ZERO)
                    .discountAmount(itemRequest.getDiscountAmount() != null ? itemRequest.getDiscountAmount() : BigDecimal.ZERO)
                    .build();

            purchaseOrder.getItems().add(item);
            totalAmount = totalAmount.add(item.getLineTotal());
        }

        purchaseOrder.setTotalAmount(totalAmount);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return convertToResponse(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderById(Long poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));
        return convertToResponse(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getPurchaseOrdersBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplier_SupplierId(supplierId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getPurchaseOrdersByStatus(PurchaseOrder.OrderStatus status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getPendingOrders() {
        return purchaseOrderRepository.findPendingOrders().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderResponse approvePurchaseOrder(Long poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != PurchaseOrder.OrderStatus.DRAFT &&
                purchaseOrder.getStatus() != PurchaseOrder.OrderStatus.SUBMITTED) {
            throw new IllegalStateException("Can only approve DRAFT or SUBMITTED purchase orders");
        }

        purchaseOrder.setStatus(PurchaseOrder.OrderStatus.APPROVED);
        purchaseOrder.setApprovedBy(getCurrentUser());
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return convertToResponse(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse receivePurchaseOrder(Long poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != PurchaseOrder.OrderStatus.APPROVED) {
            throw new IllegalStateException("Can only receive APPROVED purchase orders");
        }

        // Update inventory for each item
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByProduct_ProductIdAndWarehouse_WarehouseId(
                            item.getProduct().getProductId(),
                            purchaseOrder.getWarehouse().getWarehouseId())
                    .orElse(Inventory.builder()
                            .product(item.getProduct())
                            .warehouse(purchaseOrder.getWarehouse())
                            .quantityOnHand(0)
                            .quantityReserved(0)
                            .build());

            Integer oldQuantity = inventory.getQuantityOnHand();
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() + item.getQuantityOrdered());
            inventory.setLastRestockDate(LocalDate.now());
            inventoryRepository.save(inventory);

            // Update received quantity
            item.setQuantityReceived(item.getQuantityOrdered());

            // Record transaction
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .product(item.getProduct())
                    .warehouse(purchaseOrder.getWarehouse())
                    .transactionType(InventoryTransaction.TransactionType.PURCHASE)
                    .referenceId(purchaseOrder.getPoId())
                    .referenceType("PURCHASE_ORDER")
                    .quantityChange(item.getQuantityOrdered())
                    .quantityBefore(oldQuantity)
                    .quantityAfter(inventory.getQuantityOnHand())
                    .unitCost(item.getUnitPrice())
                    .notes("Received from PO: " + purchaseOrder.getPoNumber())
                    .build();

            transactionRepository.save(transaction);
        }

        purchaseOrder.setStatus(PurchaseOrder.OrderStatus.RECEIVED);
        purchaseOrder.setActualDeliveryDate(LocalDate.now());
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return convertToResponse(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse cancelPurchaseOrder(Long poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() == PurchaseOrder.OrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot cancel received purchase orders");
        }

        purchaseOrder.setStatus(PurchaseOrder.OrderStatus.CANCELLED);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return convertToResponse(purchaseOrder);
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(Long poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() == PurchaseOrder.OrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot delete received purchase orders");
        }

        purchaseOrderRepository.delete(purchaseOrder);
    }

    private String generatePONumber() {
        long count = purchaseOrderRepository.count();
        return "PO-" + LocalDate.now().getYear() + "-" + String.format("%06d", count + 1);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private PurchaseOrderResponse convertToResponse(PurchaseOrder po) {
        return PurchaseOrderResponse.builder()
                .poId(po.getPoId())
                .poNumber(po.getPoNumber())
                .supplierId(po.getSupplier().getSupplierId())
                .supplierName(po.getSupplier().getSupplierName())
                .warehouseId(po.getWarehouse().getWarehouseId())
                .warehouseName(po.getWarehouse().getWarehouseName())
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .actualDeliveryDate(po.getActualDeliveryDate())
                .status(po.getStatus().name())
                .totalAmount(po.getTotalAmount())
                .taxAmount(po.getTaxAmount())
                .shippingCost(po.getShippingCost())
                .notes(po.getNotes())
                .createdBy(po.getCreatedBy() != null ? po.getCreatedBy().getUsername() : null)
                .approvedBy(po.getApprovedBy() != null ? po.getApprovedBy().getUsername() : null)
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }
}
