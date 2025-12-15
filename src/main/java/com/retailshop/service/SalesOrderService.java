package com.retailshop.service;

import com.retailshop.dto.request.SalesOrderItemRequest;
import com.retailshop.dto.request.SalesOrderRequest;
import com.retailshop.dto.response.SalesOrderResponse;
import com.retailshop.entity.*;
import com.retailshop.exception.InsufficientStockException;
import com.retailshop.exception.ResourceNotFoundException;
import com.retailshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderService implements ISalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final IInventoryService inventoryService;

    @Override
    @Transactional
    public SalesOrderResponse createSalesOrder(SalesOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        User currentUser = getCurrentUser();

        // Check stock availability for all items
        for (SalesOrderItemRequest itemRequest : request.getItems()) {
            if (!inventoryService.checkStockAvailability(
                    itemRequest.getProductId(),
                    request.getWarehouseId(),
                    itemRequest.getQuantity())) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
            }
        }

        // Generate SO number
        String soNumber = generateSONumber();

        SalesOrder salesOrder = SalesOrder.builder()
                .soNumber(soNumber)
                .customer(customer)
                .warehouse(warehouse)
                .orderDate(LocalDateTime.now())
                .deliveryDate(request.getDeliveryDate())
                .status(SalesOrder.OrderStatus.PENDING)
                .paymentStatus(SalesOrder.PaymentStatus.UNPAID)
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .items(new ArrayList<>())
                .build();

        salesOrder = salesOrderRepository.save(salesOrder);

        // Add items and calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        for (SalesOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            SalesOrderItem item = SalesOrderItem.builder()
                    .salesOrder(salesOrder)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .taxRate(itemRequest.getTaxRate() != null ? itemRequest.getTaxRate() : BigDecimal.ZERO)
                    .discountPercent(itemRequest.getDiscountPercent() != null ? itemRequest.getDiscountPercent() : BigDecimal.ZERO)
                    .build();

            salesOrder.getItems().add(item);

            BigDecimal lineTotal = item.getLineTotal();
            subtotal = subtotal.add(lineTotal);
            taxAmount = taxAmount.add(lineTotal.multiply(item.getTaxRate()).divide(new BigDecimal(100)));

            // Reserve stock
            inventoryService.reserveStock(product.getProductId(), warehouse.getWarehouseId(), itemRequest.getQuantity());
        }

        salesOrder.setSubtotal(subtotal);
        salesOrder.setTaxAmount(taxAmount);
        salesOrder.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        salesOrder.setTotalAmount(subtotal.add(taxAmount).subtract(salesOrder.getDiscountAmount()));

        salesOrder = salesOrderRepository.save(salesOrder);

        return convertToResponse(salesOrder);
    }

    @Override
    @Transactional
    public SalesOrderResponse confirmSalesOrder(Long soId) {
        SalesOrder salesOrder = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() != SalesOrder.OrderStatus.PENDING) {
            throw new IllegalStateException("Can only confirm PENDING sales orders");
        }
        salesOrder.setStatus(SalesOrder.OrderStatus.CONFIRMED);
        salesOrder = salesOrderRepository.save(salesOrder);

        return convertToResponse(salesOrder);
    }

    @Override
    @Transactional
    public SalesOrderResponse shipSalesOrder(Long soId) {
        SalesOrder salesOrder = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() != SalesOrder.OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Can only ship CONFIRMED sales orders");
        }

        // Deduct from inventory
        for (SalesOrderItem item : salesOrder.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByProduct_ProductIdAndWarehouse_WarehouseId(
                            item.getProduct().getProductId(),
                            salesOrder.getWarehouse().getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

            Integer oldQuantity = inventory.getQuantityOnHand();
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - item.getQuantity());
            inventory.setQuantityReserved(inventory.getQuantityReserved() - item.getQuantity());
            inventoryRepository.save(inventory);

            // Record transaction
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .product(item.getProduct())
                    .warehouse(salesOrder.getWarehouse())
                    .transactionType(InventoryTransaction.TransactionType.SALE)
                    .referenceId(salesOrder.getSoId())
                    .referenceType("SALES_ORDER")
                    .quantityChange(-item.getQuantity())
                    .quantityBefore(oldQuantity)
                    .quantityAfter(inventory.getQuantityOnHand())
                    .unitCost(item.getProduct().getCostPrice())
                    .notes("Sale from SO: " + salesOrder.getSoNumber())
                    .build();

            transactionRepository.save(transaction);
        }

        salesOrder.setStatus(SalesOrder.OrderStatus.SHIPPED);
        salesOrder = salesOrderRepository.save(salesOrder);

        return convertToResponse(salesOrder);
    }

    @Override
    @Transactional
    public SalesOrderResponse deliverSalesOrder(Long soId) {
        SalesOrder salesOrder = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() != SalesOrder.OrderStatus.SHIPPED) {
            throw new IllegalStateException("Can only deliver SHIPPED sales orders");
        }

        salesOrder.setStatus(SalesOrder.OrderStatus.DELIVERED);
        salesOrder.setDeliveryDate(LocalDate.now());
        salesOrder = salesOrderRepository.save(salesOrder);

        return convertToResponse(salesOrder);
    }

    @Override
    @Transactional
    public SalesOrderResponse cancelSalesOrder(Long soId) {
        SalesOrder salesOrder = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() == SalesOrder.OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel delivered sales orders");
        }

        // Release reserved stock if not shipped
        if (salesOrder.getStatus() != SalesOrder.OrderStatus.SHIPPED) {
            for (SalesOrderItem item : salesOrder.getItems()) {
                inventoryService.releaseReservedStock(
                        item.getProduct().getProductId(),
                        salesOrder.getWarehouse().getWarehouseId(),
                        item.getQuantity());
            }
        }

        salesOrder.setStatus(SalesOrder.OrderStatus.CANCELLED);
        salesOrder = salesOrderRepository.save(salesOrder);

        return convertToResponse(salesOrder);
    }

    @Override
    @Transactional
    public SalesOrderResponse updateSalesOrder(Long soId, SalesOrderRequest request) {
        SalesOrder salesOrder = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() != SalesOrder.OrderStatus.PENDING) {
            throw new IllegalStateException("Can only update PENDING sales orders");
        }

        // Release old reservations
        for (SalesOrderItem item : salesOrder.getItems()) {
            inventoryService.releaseReservedStock(
                    item.getProduct().getProductId(),
                    salesOrder.getWarehouse().getWarehouseId(),
                    item.getQuantity());
        }

        salesOrder.setDeliveryDate(request.getDeliveryDate());
        salesOrder.setPaymentMethod(request.getPaymentMethod());
        salesOrder.setNotes(request.getNotes());

        // Clear and update items
        salesOrder.getItems().clear();
        salesOrderItemRepository.deleteAll(salesOrderItemRepository.findBySalesOrder_SoId(soId));

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        for (SalesOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            SalesOrderItem item = SalesOrderItem.builder()
                    .salesOrder(salesOrder)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .taxRate(itemRequest.getTaxRate() != null ? itemRequest.getTaxRate() : BigDecimal.ZERO)
                    .discountPercent(itemRequest.getDiscountPercent() != null ? itemRequest.getDiscountPercent() : BigDecimal.ZERO)
                    .build();

            salesOrder.getItems().add(item);

            BigDecimal lineTotal = item.getLineTotal();
            subtotal = subtotal.add(lineTotal);
            taxAmount = taxAmount.add(lineTotal.multiply(item.getTaxRate()).divide(new BigDecimal(100)));

            // Reserve new stock
            inventoryService.reserveStock(product.getProductId(), salesOrder.getWarehouse().getWarehouseId(), itemRequest.getQuantity());
        }

        salesOrder.setSubtotal(subtotal);
        salesOrder.setTaxAmount(taxAmount);
        salesOrder.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        salesOrder.setTotalAmount(subtotal.add(taxAmount).subtract(salesOrder.getDiscountAmount()));

        salesOrder = salesOrderRepository.save(salesOrder);

        return convertToResponse(salesOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrderResponse getSalesOrderById(Long soId) {
        SalesOrder salesOrder = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));
        return convertToResponse(salesOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getAllSalesOrders() {
        return salesOrderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getSalesOrdersByCustomer(Long customerId) {
        return salesOrderRepository.findByCustomer_CustomerId(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getSalesOrdersByStatus(SalesOrder.OrderStatus status) {
        return salesOrderRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getPendingOrders() {
        return salesOrderRepository.findPendingOrders().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSalesOrder(Long soId) {
        SalesOrder salesOrder = salesOrderRepository.findById(soId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() == SalesOrder.OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot delete delivered sales orders");
        }

        salesOrderRepository.delete(salesOrder);
    }

    private String generateSONumber() {
        long count = salesOrderRepository.count();
        return "SO-" + LocalDate.now().getYear() + "-" + String.format("%06d", count + 1);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private SalesOrderResponse convertToResponse(SalesOrder so) {
        return SalesOrderResponse.builder()
                .soId(so.getSoId())
                .soNumber(so.getSoNumber())
                .customerId(so.getCustomer().getCustomerId())
                .customerName(so.getCustomer().getCustomerName())
                .warehouseId(so.getWarehouse().getWarehouseId())
                .warehouseName(so.getWarehouse().getWarehouseName())
                .orderDate(so.getOrderDate())
                .deliveryDate(so.getDeliveryDate())
                .status(so.getStatus().name())
                .paymentStatus(so.getPaymentStatus().name())
                .paymentMethod(so.getPaymentMethod())
                .subtotal(so.getSubtotal())
                .taxAmount(so.getTaxAmount())
                .discountAmount(so.getDiscountAmount())
                .totalAmount(so.getTotalAmount())
                .notes(so.getNotes())
                .createdBy(so.getCreatedBy() != null ? so.getCreatedBy().getUsername() : null)
                .createdAt(so.getCreatedAt())
                .updatedAt(so.getUpdatedAt())
                .build();
    }
}
